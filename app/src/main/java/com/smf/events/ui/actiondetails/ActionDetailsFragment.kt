package com.smf.events.ui.actiondetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.smf.events.ui.actiondetails.model.ActionDetails
import com.smf.events.ui.quotebriefdialog.QuoteBriefDialog
import com.smf.events.ui.quotedetailsdialog.QuoteDetailsDialog
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
    var bidRequested: String = ""
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
        actionDeatilsVariableSetUp()

    }

    override fun onStart() {
        super.onStart()
        //Token Class CallBack Initialization
        tokens.setCallBackInterface(this)
        //Method For Token Validation
        apiTokenValidationNewRequest()
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
                newRequestApiCall()
            })
    }

    // Method For ActionDetails RecyclerView
    private fun myActionsStatusRecycler() {
        actionDetailsAdapter = ActionDetailsAdapter(requireContext())
        myActionDetailsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        myActionDetailsRecyclerView.adapter = actionDetailsAdapter
        actionDetailsAdapter.setCallBackInterface(this)

    }

    // Method For Prepare ActionDetails List
    private fun getActionsDetailsList(): ArrayList<ActionDetails> {
        var list = ArrayList<ActionDetails>()

        for (i in myList.indices) {
            list.add(
                ActionDetails(
                    myList[i].bidRequestId,
                    myList[i].serviceCategoryId,
                    myList[i].eventId,
                    myList[i].eventDate,
                    myList[i].eventName,
                    myList[i].serviceName,
                    myList[i].serviceDate,
                    myList[i].bidRequestedDate,
                    myList[i].biddingCutOffDate,
                    myList[i].costingType,
                    myList[i].cost,
                    myList[i].latestBidValue,
                    myList[i].bidStatus,
                    myList[i].isExistingUser,
                    myList[i].eventServiceDescriptionId,
                    myList[i].branchName,
                    myList[i].timeLeft
                )
            )
        }

        return list

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
        Log.d(QuoteDetailsDialog.TAG, "PostQuoteDetails: $idToken")

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
                        Log.d("TAG", "check token result: ${(apiResponse.response)}")
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
        bidRequested = args?.getString("bidRequested").toString()

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
    private fun apiTokenValidationNewRequest() {
        tokens.checkTokenExpiry(
            requireActivity().applicationContext as SMFApp,
            "newRequestApiCall", idToken
        )
    }

    // Method For New Request Api Call
    private fun newRequestApiCall() {
        getViewModel().getNewRequest(
            idToken,
            spRegId,
            serviceCategoryId,
            serviceVendorOnboardingId,
            bidRequested
        ).observe(viewLifecycleOwner, Observer { apiResponse ->
            when (apiResponse) {
                is ApisResponse.Success -> {
                    Log.d(
                        "TAG",
                        "sample ActionsAndStatusFragment size: ${apiResponse.response.data.serviceProviderBidRequestDtos.size}"
                    )
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
    private fun recyclerViewListUpdate(serviceProviderBidRequestDtos: List<ServiceProviderBidRequestDto>) {
        myList = serviceProviderBidRequestDtos as ArrayList
        newRequestCount = myList.size
        mDataBinding?.textNewRequest?.text = "$newRequestCount new request"
        val listActions = getActionsDetailsList()
        actionDetailsAdapter.refreshItems(listActions)
    }

    // Callback From Token Class
    override suspend fun tokenCallBack(idToken: String, caller: String) {
        withContext(Dispatchers.Main) {
            when (caller) {
                "newRequestApiCall" -> newRequestApiCall()
                else -> {}
            }
        }
    }

}