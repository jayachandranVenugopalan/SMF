package com.smf.events.ui.actionandstatusdashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.SMFApp
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentActionsAndStatusBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.helper.AppConstants
import com.smf.events.helper.Tokens
import com.smf.events.ui.actionandstatusdashboard.adapter.ActionsAdapter
import com.smf.events.ui.actiondetails.ActionDetailsFragment
import com.smf.events.ui.dashboard.adapter.StatusAdaptor
import com.smf.events.ui.dashboard.model.ActionAndStatusCount
import com.smf.events.ui.dashboard.model.MyEvents
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ActionsAndStatusFragment :
    BaseFragment<FragmentActionsAndStatusBinding, ActionsAndStatusViewModel>(),
    ActionsAdapter.OnActionCardClickListener, Tokens.IdTokenCallBackInterface {

    private lateinit var myActionRecyclerView: RecyclerView
    lateinit var actionAdapter: ActionsAdapter
    private lateinit var myStatusRecyclerView: RecyclerView
    lateinit var statusAdapter: StatusAdaptor
    lateinit var actionAndStatusData: ActionAndStatusCount
    var spRegId: Int = 0
    lateinit var idToken: String
    var roleId: Int = 0
    var serviceCategoryId: Int? = null
    var serviceVendorOnboardingId: Int? = null
    var newRequestCount: Int = 0

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var tokens: Tokens

    override fun getViewModel(): ActionsAndStatusViewModel =
        ViewModelProvider(this, factory).get(ActionsAndStatusViewModel::class.java)

    override fun getBindingVariable(): Int = BR.actionsAndStatusViewModel

    override fun getContentView(): Int = R.layout.fragment_actions_and_status

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setIdTokenAndSpRegId()
        serviceCategoryIdAndServiceOnboardingIdSetup()

    }

    override fun onStart() {
        super.onStart()
        // Token Class CallBack Initialization
        tokens.setCallBackInterface(this)
        // Action And Status Api Call
        apiTokenValidationActionAndStatus()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initializing actions recyclerview
        myActionRecyclerView = mDataBinding?.actionsRecyclerview!!

        //Initializing status recyclerview
        myStatusRecyclerView = mDataBinding?.statusRecyclerview!!

        //Actions  Recycler view
        myActionsStatusRecycler()

        //Status Recycler view
        myStatusRecycler()


    }

    // Method For ActionsStatusRecyclerView SetUp
    private fun myActionsStatusRecycler() {
        actionAdapter = ActionsAdapter(requireContext())
        myActionRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        myActionRecyclerView.adapter = actionAdapter
        actionAdapter.setOnClickListener(this)

    }

    // Method For StatusRecyclerView SetUp
    private fun myStatusRecycler() {
        statusAdapter = StatusAdaptor()
        myStatusRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        myStatusRecyclerView.adapter = statusAdapter

    }

    // Prepare Action List Values
    private fun getActionsList(actionAndStatusData: ActionAndStatusCount?): ArrayList<MyEvents> {
        var list = ArrayList<MyEvents>()
        list.add(MyEvents(actionAndStatusData?.bidRequestedActionsCount.toString(), "New request"))
        newRequestCount = actionAndStatusData!!.bidRequestedActionsCount
        list.add(
            MyEvents(
                actionAndStatusData.pendingForQuoteActionCount.toString(),
                "Pending Quote"
            )
        )
        list.add(MyEvents(actionAndStatusData.wonBidStatusCount.toString(), "Won Bid"))
        list.add(MyEvents(actionAndStatusData.bidRejectedActionCount.toString(), "Rejected"))
        list.add(MyEvents(actionAndStatusData.bidSubmittedActionCount.toString(), "Bid Submitted"))

        return list

    }

    // Prepare Status List Values
    private fun getStatusList(actionAndStatusData: ActionAndStatusCount): ArrayList<MyEvents> {
        var list = ArrayList<MyEvents>()
        list.add(
            MyEvents(
                actionAndStatusData.bidSubmittedStatusCount.toString(),
                "Bids Submitted"
            )
        )
        list.add(MyEvents(actionAndStatusData.serviceDoneStatusCount.toString(), "Service done"))
        list.add(MyEvents(actionAndStatusData.bidTimedOutStatusCount.toString(), "Bid TimeOut"))
        list.add(MyEvents(actionAndStatusData.wonBidStatusCount.toString(), "Won Bid"))
        list.add(MyEvents(actionAndStatusData.bidRejectedStatusCount.toString(), "Rejected"))
        list.add(MyEvents(actionAndStatusData.lostBidStatusCount.toString(), "Lost Bid"))

        return list

    }

    // Action Card Click Listener Interface Method
    override fun actionCardClick(myEvents: MyEvents) {
        when (myEvents.titleText) {
            "New request" -> {
                apiTokenValidationNewRequest()
            }
            else -> {
                Log.d("TAG", "newRequestApiCallsample :else block")
            }
        }

    }

    // Token Validation For NewRequest Api Call
    private fun apiTokenValidationNewRequest() {
        if (idToken.isNotEmpty()) {
            Log.d("TAG", "onResume: called")
            tokens.checkTokenExpiry(
                requireActivity().applicationContext as SMFApp,
                "newRequest", idToken
            )
        }
    }

    // Method For AWS Token Validation Action And Status
    private fun apiTokenValidationActionAndStatus() {
        if (idToken.isNotEmpty()) {
            Log.d("TAG", "onResume: called")
            tokens.checkTokenExpiry(
                requireActivity().applicationContext as SMFApp,
                "actionAndStatus", idToken
            )
        }
    }

    // Method For Set IdToken And SpRegId From SharedPreferences
    private fun setIdTokenAndSpRegId() {
        var getSharedPreferences = requireActivity().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )
        spRegId = getSharedPreferences.getInt("spRegId", 0)
        idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
        roleId = getSharedPreferences.getInt("roleId", 0)
    }

    // Method For ApiCall For Action And Status Counts
    private fun actionAndStatusApiCall() {
        getViewModel().getActionAndStatus(
            idToken,
            spRegId,
            serviceCategoryId,
            serviceVendorOnboardingId
        )
            .observe(viewLifecycleOwner, Observer { apiResponse ->
                when (apiResponse) {
                    is ApisResponse.Success -> {
                        actionAndStatusData = ActionAndStatusCount(
                            apiResponse.response.actionandStatus.bidRequestedActionsCount,
                            apiResponse.response.actionandStatus.bidSubmittedStatusCount,
                            apiResponse.response.actionandStatus.bidSubmittedActionCount,
                            apiResponse.response.actionandStatus.bidRejectedStatusCount,
                            apiResponse.response.actionandStatus.bidRejectedActionCount,
                            apiResponse.response.actionandStatus.pendingForQuoteActionCount,
                            apiResponse.response.actionandStatus.wonBidStatusCount,
                            apiResponse.response.actionandStatus.lostBidStatusCount,
                            apiResponse.response.actionandStatus.bidTimedOutStatusCount,
                            apiResponse.response.actionandStatus.serviceDoneStatusCount,
                            apiResponse.response.actionandStatus.statusCount,
                            apiResponse.response.actionandStatus.actionCount
                        )

                        recyclerViewListUpdate()
                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })
    }

    // Method For Update Action And Status Count To RecyclerView List
    private fun recyclerViewListUpdate() {

        val listActions1 = getActionsList(actionAndStatusData)
        actionAdapter.refreshItems(listActions1)

        val listStatus = getStatusList(actionAndStatusData)
        statusAdapter.refreshItems(listStatus)

        mDataBinding?.txPendtingitems?.text =
            "${actionAndStatusData?.actionCount} PendingItems"
        mDataBinding?.txPendingstatus?.text =
            "${actionAndStatusData?.statusCount} Status"
    }

    // Method For ServiceCategoryId And ServiceOnboardId Initialization
    private fun serviceCategoryIdAndServiceOnboardingIdSetup() {

        val args = arguments
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

    // Callback From Token Class
    override suspend fun tokenCallBack(idToken: String, caller: String) {
        withContext(Dispatchers.Main) {
            when (caller) {
                "newRequest" -> goToActionDetailsFragment(AppConstants.BID_REQUESTED)
                "actionAndStatus" -> actionAndStatusApiCall()
            }
        }
    }

    // Method For Calling ActionDetailsFragment With Action Details
    private fun goToActionDetailsFragment(bidRequested: String) {
        val args = Bundle()
        args.putString("bidRequested", bidRequested)
        serviceCategoryId?.let { args.putInt("serviceCategoryId", it) }
        serviceVendorOnboardingId?.let { args.putInt("serviceVendorOnboardingId", it) }

        val actionDetailsFragment = ActionDetailsFragment()
        actionDetailsFragment.arguments = args

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.action_and_status_layout, actionDetailsFragment)
            .addToBackStack(ActionsAndStatusFragment::class.java.name)
            .commit()
    }
}