package com.smf.events.ui.actiondetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.SMFApp
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentActionDetailsBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.helper.AppConstants
import com.smf.events.helper.Tokens
import com.smf.events.ui.actionandstatusdashboard.ActionsAndStatusFragment
import com.smf.events.ui.actionandstatusdashboard.model.ServiceProviderBidRequestDto
import com.smf.events.ui.actiondetails.adapter.ActionDetailsAdapter
import com.smf.events.ui.actiondetails.adapter.ActionDetailsAdapter.CallBackInterface
import com.smf.events.ui.dashboard.DashBoardFragmentDirections
import com.smf.events.ui.quotebriefdialog.QuoteBriefDialog
import com.smf.events.ui.quotedetailsdialog.model.BiddingQuotDto
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ActionDetailsFragment :
    BaseFragment<FragmentActionDetailsBinding, ActionDetailsViewModel>(),
    Tokens.IdTokenCallBackInterface, CallBackInterface {

    private lateinit var myActionDetailsRecyclerView: RecyclerView
    lateinit var actionDetailsAdapter: ActionDetailsAdapter
    private var closeBtn: ImageView? = null
    private var myList = ArrayList<ServiceProviderBidRequestDto>()
    var serviceCategoryId: Int? = null
    var serviceVendorOnboardingId: Int? = null
    var newRequestCount: Int? = 0
    var bidStatus: String = ""
    lateinit var idToken: String
    var spRegId: Int = 0

    @Inject
    lateinit var tokens: Tokens

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): ActionDetailsViewModel =
        ViewModelProvider(this, factory).get(ActionDetailsViewModel::class.java)

    override fun getBindingVariable(): Int = BR.actionDetailsViewModel

    override fun getContentView(): Int = R.layout.fragment_action_details


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Action Details required Variable setUp
        actionDeatilsVariableSetUp()

    }

    override fun onStart() {
        super.onStart()
        //Token Class CallBack Initialization
        tokens.setCallBackInterface(this)
        //Method For Token Validation
        apiTokenValidationBidActions()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeBtn = mDataBinding?.closeBtn
        //Initializing actions recyclerview
        myActionDetailsRecyclerView = mDataBinding?.actionDetailsRecyclerview!!
        //Close Button Click Listener
        clickListeners()
        //Actions Recycler view
        myActionsStatusRecycler()
    }

    override fun onResume() {
        super.onResume()
        // ResultListener For Observe Data From Dialogs
        parentFragmentManager.setFragmentResultListener("1", viewLifecycleOwner,
            FragmentResultListener { requestKey: String, result: Bundle ->
                apiTokenValidationBidActions()
            })
    }

    // Method For ActionDetails RecyclerView
    private fun myActionsStatusRecycler() {
        actionDetailsAdapter = ActionDetailsAdapter(requireContext(), bidStatus)
        myActionDetailsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        myActionDetailsRecyclerView.adapter = actionDetailsAdapter
        actionDetailsAdapter.setCallBackInterface(this)

    }


    // Close Button ClickListener
    private fun clickListeners() {
        closeBtn?.setOnClickListener {
            var args = Bundle()
            serviceCategoryId?.let { it1 -> args.putInt("serviceCategoryId", it1) }
            serviceVendorOnboardingId?.let { it1 -> args.putInt("serviceVendorOnboardingId", it1) }
            var actionAndStatusFragment = ActionsAndStatusFragment()
            actionAndStatusFragment.arguments = args

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.action_and_status_layout, actionAndStatusFragment)
                .setReorderingAllowed(true)
                .commit()
        }
    }

    // Callback From ActionDetails Adapter
    override fun callBack(
        status: String,
        bidRequestId: Int,
        costingType: String,
        bidStatus: String,
        cost: String?,
        latestBidValue: String?,
        branchName: String,
    ) {
        postQuoteDetails(bidRequestId, costingType, bidStatus, cost, latestBidValue, branchName)

    }

    // Method For postQuoteDetails Api Call
    fun postQuoteDetails(
        bidRequestId: Int,
        costingType: String,
        bidStatus: String,
        cost: String?,
        latestBidValue: String?,
        branchName: String,
    ) {
        val getSharedPreferences = requireContext().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )

        val idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
        val biddingQuote = BiddingQuotDto(
            bidRequestId,
            AppConstants.BID_SUBMITTED,
            branchName,
            "",
            cost,
            costingType,
            "USD($)",
            null,
            null,
            null,
            null,
            0
        )
        getViewModel().postQuoteDetails(idToken, bidRequestId, biddingQuote)
            .observe(viewLifecycleOwner, Observer { apiResponse ->
                when (apiResponse) {
                    is ApisResponse.Success -> {
                        QuoteBriefDialog.newInstance()
                            .show(
                                (context as androidx.fragment.app.FragmentActivity).supportFragmentManager,
                                QuoteBriefDialog.TAG
                            )
                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })
    }

    // Method For Set ActionDetails Frag Values From ActionAndStatusFrag And SharedPreferences
    private fun actionDeatilsVariableSetUp() {
        val args = arguments
        bidStatus = args?.getString("bidStatus").toString()

        val getSharedPreferences = requireContext().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )
        idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
        spRegId = getSharedPreferences.getInt("spRegId", 0)

        if (args?.getInt("serviceCategoryId") == 0) {
            if (args.getInt("serviceVendorOnboardingId") == 0) {
                serviceCategoryId = null
                serviceVendorOnboardingId = null
            }
        } else if (args?.getInt("serviceCategoryId") != 0 && args?.getInt("serviceVendorOnboardingId") == 0) {
            serviceCategoryId = args.getInt("serviceCategoryId")
            serviceVendorOnboardingId = null
        } else {
            serviceCategoryId = args?.getInt("serviceCategoryId")
            serviceVendorOnboardingId = args?.getInt("serviceVendorOnboardingId")

        }
    }

    // Method For AWS Token Validation
    private fun apiTokenValidationBidActions() {
        tokens.checkTokenExpiry(
            requireActivity().applicationContext as SMFApp,
            "bidStatus", idToken
        )
    }

    // Method For New Request Api Call
    private fun bidActionsApiCall() {
        getViewModel().getBidActions(
            idToken,
            spRegId,
            serviceCategoryId,
            serviceVendorOnboardingId,
            bidStatus
        ).observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    recyclerViewListUpdate(apiResponse.response.data.serviceProviderBidRequestDtos)
                }
                is ApisResponse.Error -> {
                    Log.d("TAG", "check token result: ${apiResponse.exception}")
                }
                else -> {
                }
            }
        })
    }

    // Method For Action Details RecyclerView List Update
    private fun recyclerViewListUpdate(serviceProviderBidRequestDtos: List<ServiceProviderBidRequestDto>?) {
        myList = settingBidActionsList(serviceProviderBidRequestDtos)
        newRequestCount = myList.size
        when (bidStatus) {
            AppConstants.BID_REQUESTED -> mDataBinding?.textNewRequest?.text =
                "$newRequestCount New request"
            AppConstants.PENDING_FOR_QUOTE -> mDataBinding?.textNewRequest?.text =
                "$newRequestCount pending quote"
            AppConstants.BID_REJECTED -> mDataBinding?.textNewRequest?.text =
                "$newRequestCount Rejections"
            AppConstants.BID_SUBMITTED -> mDataBinding?.textNewRequest?.text =
                "$newRequestCount Bid Submitted"
        }
        val listActions = getViewModel().getActionsDetailsList(myList)
        actionDetailsAdapter.refreshItems(listActions)
    }

    //Setting Bid action list if the value is null
    private fun settingBidActionsList(serviceProviderBidRequestDtos: List<ServiceProviderBidRequestDto>?): ArrayList<ServiceProviderBidRequestDto> {
        return if (serviceProviderBidRequestDtos.isNullOrEmpty()) {
            ArrayList()
        } else {
            serviceProviderBidRequestDtos as ArrayList
        }
    }

    // Callback From Token Class
    override suspend fun tokenCallBack(idToken: String, caller: String) {
        withContext(Dispatchers.Main) {
            when (caller) {
                "bidStatus" -> bidActionsApiCall()
                "signOut" -> moveToSignInScreen()
            }
        }
    }

    // Method For User SignOut And Moved To SignIn Screen
    private fun moveToSignInScreen() {
        Log.d("TAG", "checkTokenExpiry refereshTokentime move to signIn screen")
        val action = DashBoardFragmentDirections.actionDashBoardFragmentToSignInFragment()
        findNavController().navigate(action)
    }

}