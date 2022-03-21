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
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentActionsAndStatusBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.helper.AppConstants
import com.smf.events.rxbus.RxBus
import com.smf.events.rxbus.RxEvent
import com.smf.events.ui.actionandstatusdashboard.adapter.ActionsAdapter
import com.smf.events.ui.actionandstatusdashboard.model.ServiceProviderBidRequestDto
import com.smf.events.ui.actiondetails.ActionDetailsFragment
import com.smf.events.ui.dashboard.adapter.StatusAdaptor
import com.smf.events.ui.dashboard.model.ActionAndStatusCount
import com.smf.events.ui.dashboard.model.MyEvents
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ActionsAndStatusFragment :
    BaseFragment<FragmentActionsAndStatusBinding, ActionsAndStatusViewModel>(),
    ActionsAdapter.OnActionCardClickListener {

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

    @Inject
    lateinit var factory: ViewModelProvider.Factory

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
        Log.d("TAG", "onCreate: ActionsAndStatusFragment called")
        setIdTokenAndSpRegId()
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG", "onCreate: ActionsAndStatusFragment called onstart $serviceCategoryId")
        Log.d("TAG", "onCreate: ActionsAndStatusFragment called onstart $serviceVendorOnboardingId")
        actionAndStatusApiCall()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionAndStatusData = ActionAndStatusCount(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        //Initializing actions recyclerview
        myActionRecyclerView = mDataBinding?.actionsRecyclerview!!

        //Initializing status recyclerview
        myStatusRecyclerView = mDataBinding?.statusRecyclerview!!

        //Actions  Recycler view
        myActionsStatusRecycler()

        //Status Recycler view
        myStatusRecycler()

        val listActions = getActionsList(actionAndStatusData)
        actionAdapter.refreshItems(listActions)

        val listStatus = getStatusList(actionAndStatusData)
        statusAdapter.refreshItems(listStatus)


    }

    private fun myActionsStatusRecycler() {
        actionAdapter = ActionsAdapter(requireContext())
        myActionRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        myActionRecyclerView.adapter = actionAdapter
        actionAdapter.setOnClickListener(this)

    }

    private fun myStatusRecycler() {
        statusAdapter = StatusAdaptor()
        myStatusRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        myStatusRecyclerView.adapter = statusAdapter

    }

    private fun getActionsList(actionAndStatusData: ActionAndStatusCount?): ArrayList<MyEvents> {
        var list = ArrayList<MyEvents>()
        list.add(MyEvents(actionAndStatusData?.bidRequestedActionsCount.toString(), "New request"))
        list.add(
            MyEvents(
                actionAndStatusData?.pendingForQuoteActionCount.toString(),
                "Send Quotes"
            )
        )
        list.add(MyEvents(actionAndStatusData?.wonBidStatusCount.toString(), "Won Bid"))
        list.add(MyEvents(actionAndStatusData?.bidRejectedActionCount.toString(), "Rejected"))
        list.add(MyEvents(actionAndStatusData?.bidSubmittedActionCount.toString(), "Bid Submitted"))

        return list

    }

    private fun getStatusList(actionAndStatusData: ActionAndStatusCount): ArrayList<MyEvents> {
        var list = ArrayList<MyEvents>()
        list.add(
            MyEvents(
                actionAndStatusData?.bidSubmittedStatusCount.toString(),
                "Bids Submitted"
            )
        )
        list.add(MyEvents(actionAndStatusData?.serviceDoneStatusCount.toString(), "Service done"))
        list.add(MyEvents(actionAndStatusData?.bidTimedOutStatusCount.toString(), "Bid TimeOut"))
        list.add(MyEvents(actionAndStatusData?.wonBidStatusCount.toString(), "Won Bid"))
        list.add(MyEvents(actionAndStatusData?.bidRejectedStatusCount.toString(), "Rejected"))
        list.add(MyEvents(actionAndStatusData?.lostBidStatusCount.toString(), "Lost Bid"))

        return list

    }

    // Action Card Click Listener Interface Method
    override fun actionCardClick(myEvents: MyEvents) {
        when (myEvents.titleText) {
            "New request" -> {
                newRequestApiCall(AppConstants.BID_REQUESTED)
            }
            else -> {
                Log.d("TAG", "newRequestApiCallsample :else block")
            }
        }

    }

    private fun newRequestApiCall(bidRequested: String) {
        getViewModel().getNewRequest(
            idToken,
            spRegId,
            serviceCategoryId,
            serviceVendorOnboardingId,
            bidRequested
        )
            .observe(viewLifecycleOwner, Observer { apiResponse ->
                when (apiResponse) {
                    is ApisResponse.Success -> {
                        Log.d(
                            "TAG",
                            "newRequestApiCall : ${apiResponse.response.data.serviceProviderBidRequestDtos}"
                        )
                        goToActionDetailsFragmentWithDetails(apiResponse.response.data.serviceProviderBidRequestDtos)
                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })
    }

    // Method For Calling ActionDetailsFragment With Action Details
    private fun goToActionDetailsFragmentWithDetails(serviceProviderBidRequestDtos: List<ServiceProviderBidRequestDto>) {
        val args = Bundle()
        args.putParcelableArrayList("list", serviceProviderBidRequestDtos as ArrayList)

        val actionDetailsFragment = ActionDetailsFragment()
        actionDetailsFragment.arguments = args

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.action_and_status_layout, actionDetailsFragment)
            .addToBackStack(ActionsAndStatusFragment::class.java.name)
            .commit()
    }

    private fun setIdTokenAndSpRegId() {
        var getSharedPreferences = requireActivity().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )
        spRegId = getSharedPreferences.getInt("spRegId", 0)
        idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
        roleId = getSharedPreferences.getInt("roleId", 0)
    }

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
                        Log.d(
                            "TAG",
                            "sample ActionsAndStatusFragment: ${apiResponse.response.success}"
                        )
                        Log.d("TAG", "sample ActionsAndStatusFragment: ${apiResponse.response}")

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

                        recyclerViewListUpdation()
                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })
    }

    private fun recyclerViewListUpdation() {

//        if (it.serviceAndCategoryId.serviceCategoryId != 0) {
//            serviceCategoryId = it.serviceAndCategoryId.serviceCategoryId
//            serviceVendorOnboardingId = it.serviceAndCategoryId.serviceVendorOnboardingId
//
//        }

        val listActions1 = getActionsList(actionAndStatusData)
        actionAdapter.refreshItems(listActions1)

        val listStatus = getStatusList(actionAndStatusData)
        statusAdapter.refreshItems(listStatus)

        mDataBinding?.txPendtingitems?.text =
            "${actionAndStatusData?.actionCount.toString()} PendingItems"
        mDataBinding?.txPendingstatus?.text =
            "${actionAndStatusData?.statusCount.toString()} Status"
    }

}