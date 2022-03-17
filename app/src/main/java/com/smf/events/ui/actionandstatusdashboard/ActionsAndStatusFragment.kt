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
import com.smf.events.rxbus.RxBus
import com.smf.events.rxbus.RxEvent
import com.smf.events.ui.actionandstatusdashboard.adapter.ActionsAdapter
import com.smf.events.ui.actiondetails.ActionDetailsFragment
import com.smf.events.ui.dashboard.adapter.StatusAdaptor
import com.smf.events.ui.dashboard.model.ActionAndStatusCount
import com.smf.events.ui.dashboard.model.BranchDatas
import com.smf.events.ui.dashboard.model.DatasNew
import com.smf.events.ui.dashboard.model.MyEvents
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ActionsAndStatusFragment :
    BaseFragment<FragmentActionsAndStatusBinding, ActionsAndStatusViewModel>(),
    ActionsAdapter.OnActionCardClickListener {

    private lateinit var myActionRecyclerView: RecyclerView
    lateinit var actionAdapter: ActionsAdapter
    private lateinit var myStatusRecyclerView: RecyclerView
    lateinit var statusAdapter: StatusAdaptor
    private lateinit var actionDisposable: Disposable
    lateinit var actionAndStatusData: ActionAndStatusCount

    var spRegId: Int = 0
    lateinit var idToken: String
    var roleId: Int = 0
    var serviceCategoryId: Int? = null
    var serviceVendorOnboardingId: Int? = null
    var bidStatus: String = "BID REQUESTED"

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
        Log.d("TAG", "onCreate: called")

        setIdTokenAndSpRegId()
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

        actionDisposable = RxBus.listen(RxEvent.ActionAndStatus::class.java).subscribe {

            Log.d("TAG", "onViewCreated:${it.actionAndStatusCount} ")
            var actions = it.actionAndStatusCount
            actionAndStatusData = ActionAndStatusCount(
                actions.bidRequestedActionsCount,
                actions.bidSubmittedStatusCount,
                actions.bidSubmittedActionCount,
                actions.bidRejectedStatusCount,
                actions.bidRejectedActionCount,
                actions.pendingForQuoteActionCount,
                actions.wonBidStatusCount,
                actions.lostBidStatusCount,
                actions.bidTimedOutStatusCount,
                actions.serviceDoneStatusCount,
                actions.statusCount,
                actions.actionCount
            )

            val listActions1 = getActionsList(actionAndStatusData)
            actionAdapter.refreshItems(listActions1)

            val listStatus = getStatusList(actionAndStatusData)
            statusAdapter.refreshItems(listStatus)

            getViewModel()?.actionAndStatusCount(mDataBinding!!, actionAndStatusData)
        }


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

        showToast(myEvents.titleText)

        when (myEvents.titleText) {
            "New request" -> {
                newRequestApiCall()
            }
            else -> {
                Log.d("TAG", "newRequestApiCallsample :else block")
            }
        }

    }

    private fun newRequestApiCall() {
        getViewModel().getNewRequest(
            idToken,
            spRegId,
            serviceCategoryId,
            serviceVendorOnboardingId,
            bidStatus
        )
            .observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {
                        Log.d("TAG", "newRequestApiCallsample : ${apiResponse.response.success}")

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.action_and_status_layout, ActionDetailsFragment())
                            .addToBackStack(ActionsAndStatusFragment::class.java.name)
                            .commit()
                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })
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

    override fun onDestroy() {
        super.onDestroy()
        if (!actionDisposable.isDisposed) actionDisposable.dispose()
    }
}