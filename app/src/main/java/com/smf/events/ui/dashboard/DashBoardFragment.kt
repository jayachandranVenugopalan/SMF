package com.smf.events.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.SMFApp
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentDashBoardBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.helper.Tokens
import com.smf.events.rxbus.RxBus
import com.smf.events.rxbus.RxEvent
import com.smf.events.ui.actionandstatusdashboard.ActionsAndStatusFragment
import com.smf.events.ui.dashboard.adapter.MyEventsAdapter
import com.smf.events.ui.dashboard.model.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class DashBoardFragment : BaseFragment<FragmentDashBoardBinding, DashBoardViewModel>(),
    DashBoardViewModel.CallBackInterface, Tokens.IdTokenCallBackInterface {
    lateinit var rep: Any
    var spRegId: Int = 0
    lateinit var idToken: String
    var roleId: Int = 0
    private lateinit var myEventsRecyclerView: RecyclerView
    lateinit var adapter: MyEventsAdapter
    var serviceList = ArrayList<ServicesData>()
    var serviceCategoryId: Int = 0
    lateinit var actionAndStatusData: ActionAndStatusCount
    var branchListSpinner = ArrayList<BranchDatas>()

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): DashBoardViewModel =
        ViewModelProvider(this, factory).get(DashBoardViewModel::class.java)

    override fun getBindingVariable(): Int = BR.dashBoardViewModel

    override fun getContentView(): Int = R.layout.fragment_dash_board

    @Inject
    lateinit var tokens: Tokens
    private var pressedTime: Long = 0

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "onCreate: called")
        restrictBackButton()
        setIdTokenAndSpRegId()
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDataBinding?.myEventsLayout?.visibility = View.INVISIBLE
        mDataBinding?.spinnerAction?.visibility = View.INVISIBLE
        // Initialize IdTokenCallBackInterface
        tokens.setCallBackInterface(this)

        // DashBoard ViewModel CallBackInterface
        getViewModel().setCallBackInterface(this)

        // Initialize MyEvent Recycler
        myEventsRecycler()

        if (idToken.isNotEmpty()) {
            tokens.checkTokenExpiry(
                requireActivity().applicationContext as SMFApp,
                "event_type"
            )
        }


    }

    private fun setAllService() {

        var allServiceList: ArrayList<String> = ArrayList()
        serviceList.forEach {
            allServiceList.add(it.serviceName)
        }
        val allServices = allServiceList

        mDataBinding?.myEventsLayout?.visibility = View.VISIBLE
        mDataBinding?.spinnerAction?.visibility = View.VISIBLE
        //spinner view for allservices
        getViewModel().allServices(mDataBinding, allServices)

        //spinner view for branches
        var branchSpinner: ArrayList<String> = ArrayList()

        branchListSpinner.forEach {
            branchSpinner.add(it.branchName)
        }

        val branchdataspinner = branchSpinner
        getViewModel().branches(
            mDataBinding,
            branchdataspinner,
            idToken,
            spRegId,
            serviceCategoryId,
            0
        )

        actionAndStatusFragment()
    }


    private fun myEventsRecycler() {
        myEventsRecyclerView = mDataBinding?.eventsRecyclerView!!
        adapter = MyEventsAdapter()
        myEventsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        myEventsRecyclerView.adapter = adapter
    }

    override suspend fun tokenCallBack(idToken: String, caller: String) {
        Log.d("TAG", "check clickBtn dashboard after ")
        withContext(Main) {

            getAllServiceAndCounts()

        }
    }

    // Method for restrict user back button
    private fun restrictBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (pressedTime + 1500 > System.currentTimeMillis()) {
                requireActivity().finish()
            } else {
                showToast(getString(R.string.Press_back_again_to_exit))
            }
            pressedTime = System.currentTimeMillis()
        }
    }

    override fun callBack(idToken: String) {
        Log.d("AuthQuickStart", "Dashboard callBack called token $idToken")
        CoroutineScope(Main).launch {
            getViewModel().get184Types(idToken)
                .observe(this@DashBoardFragment, Observer { apiResponse ->
                    when (apiResponse) {
                        is ApisResponse.Success -> {
                            Log.d("TAG", "token 184 result: ${apiResponse.response.success}")
                            Log.d("TAG", "token 184 result: ${apiResponse.response.result.info}")
                            showToast("ok")
                        }
                        is ApisResponse.Error -> {
                            Log.d("TAG", "token 184 result: ${apiResponse.exception}")
                            showToast("Not ok")
                        }
                    }
                })
        }

    }

    //All Service spinner view clicked
    override fun itemClick(position: Int) {

        if (serviceList[position].serviceName == "All Service") {
            var branchSpinner: ArrayList<String> = ArrayList()
            branchSpinner.add(0, "Branches")

            val branchdataspinner = branchSpinner
            getViewModel().branches(
                mDataBinding,
                branchdataspinner,
                idToken,
                spRegId,
                serviceCategoryId,
                0
            )
            getActionAndStatus(0)
        }
        if (serviceList[position].serviceName != "All Service") {
            //getBranches()
            Toast.makeText(
                requireContext(),
                serviceList[position].serviceName,
                Toast.LENGTH_LONG
            )
                .show()
            serviceCategoryId = (serviceList[position].serviceCategoryId.toInt())
            Log.d("TAG", "itemClick: $branchListSpinner")
            getBranches(serviceCategoryId)
            branchListSpinner.clear()
        }
    }

    //Branch spinner view clicked
    override fun branchItemClick(serviceVendorOnboardingId: Int, name: String?) {

        Log.d("TAG", "branchItemClick: $serviceVendorOnboardingId")


        Log.d(
            "TAG",
            "branchItemClick serviceVendorOnboardingId: ${branchListSpinner[serviceVendorOnboardingId].branchId}"
        )

        if (name == "Branches") {
            getActionAndStatus(0)
        } else {
            getActionAndStatus(branchListSpinner[serviceVendorOnboardingId].branchId)

        }
    }

    private fun getServiceCountList(data: Datas): ArrayList<MyEvents> {
        var list = ArrayList<MyEvents>()
        list.add(MyEvents("${data.activeServiceCount}", "Active"))
        list.add(MyEvents("${data.approvalPendingServiceCount}", "Pending"))
        list.add(MyEvents("${data.draftServiceCount}", "Draft"))
        list.add(MyEvents("${data.inactiveServiceCount}", "Inactive"))
        list.add(MyEvents("${data.rejectedServiceCount}", "Rejected"))

        return list
    }

    // Action And Status UI setUp
    private fun actionAndStatusFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.action_and_status_layout, ActionsAndStatusFragment())
            .setReorderingAllowed(true)
            .commit()
    }

    //Counts And AllService ApiCall
    private fun getAllServiceAndCounts() {

        // Getting Service Provider Reg Id and Role Id
        getViewModel().getServiceCount(idToken, spRegId)
            .observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {
                        val serviceList = getServiceCountList(apiResponse.response.data)
                        adapter.refreshItems(serviceList)

                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })

        // Getting All Service
        getViewModel().getAllServices(idToken, spRegId)
            .observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {
                        Log.d("TAG", "sample: ${apiResponse.response.success}")
                        Log.d("TAG", "sample: ${apiResponse.response.data}")
                        Log.d("TAG", "sample: ${apiResponse.response.data.size}")

                        serviceList.add(ServicesData("All Service", 0))
                        branchListSpinner.add(BranchDatas("Branches", 0))
                        apiResponse.response.data.forEach {
                            serviceList.add(ServicesData(it.serviceName, it.serviceCategoryId))
                        }

                        Log.d("TAG", "sample: mapdata $serviceList")


                        setAllService()
                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })
        // getActionAndStatus(0)

    }

    //Action and Status ApiCall
    private fun getActionAndStatus(serviceVendorOnboardingId: Int) {


        getViewModel().getActionAndStatus(
            idToken,
            spRegId,
            serviceCategoryId,
            serviceVendorOnboardingId
        )
            .observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {
                        Log.d("TAG", "sample: ${apiResponse.response.success}")
                        Log.d("TAG", "sample: ${apiResponse.response}")
                        var newRequestAction: Int =
                            apiResponse.response.actionandStatus.bidRequestedActionsCount
                        var bidSubmittedStatusCount: Int =
                            apiResponse.response.actionandStatus.bidSubmittedStatusCount
                        var bidSubmittedAction: Int =
                            apiResponse.response.actionandStatus.bidSubmittedActionCount
                        var bidRejectedStatusCount: Int =
                            apiResponse.response.actionandStatus.bidRejectedStatusCount
                        var bidRejectedActionCount: Int =
                            apiResponse.response.actionandStatus.bidRejectedActionCount
                        var pendingForQuoteActionCount: Int =
                            apiResponse.response.actionandStatus.pendingForQuoteActionCount
                        var wonBidStatusCount: Int =
                            apiResponse.response.actionandStatus.wonBidStatusCount
                        var lostBidStatusCount: Int =
                            apiResponse.response.actionandStatus.lostBidStatusCount
                        var bidTimedOutStatusCount: Int =
                            apiResponse.response.actionandStatus.bidTimedOutStatusCount
                        var serviceDoneStatusCount: Int =
                            apiResponse.response.actionandStatus.serviceDoneStatusCount
                        var statusCount: Int = apiResponse.response.actionandStatus.statusCount
                        var actionCount: Int = apiResponse.response.actionandStatus.actionCount

                        actionAndStatusData = ActionAndStatusCount(
                            newRequestAction,
                            bidSubmittedStatusCount,
                            bidSubmittedAction,
                            bidRejectedStatusCount,
                            bidRejectedActionCount,
                            pendingForQuoteActionCount,
                            wonBidStatusCount,
                            lostBidStatusCount,
                            bidTimedOutStatusCount,
                            serviceDoneStatusCount,
                            statusCount,
                            actionCount
                        )


                        RxBus.publish(
                            RxEvent.ActionAndStatus(
                                actionAndStatusData, ServiceAndCategoryId(
                                    serviceCategoryId, serviceVendorOnboardingId
                                )
                            )
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

    //Branch ApiCall
    private fun getBranches(serviceCategoryId: Int) {

        getViewModel().getServicesBranches(idToken, spRegId, serviceCategoryId)
            .observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {
                        Log.d("TAG", "sample: ${apiResponse.response.success}")

                        // branchListSpinner.add(BranchDatas("Branches", 0))
                        var branchTypeItems: List<DatasNew> = apiResponse.response.datas
                        for (i in branchTypeItems.indices) {
                            val branchName: String =
                                branchTypeItems[i].branchName // I want to show this when Selected
                            val branchId: Int = branchTypeItems[i].serviceVendorOnboardingId
                            branchListSpinner.add(BranchDatas(branchName, branchId))

                        }
                        Log.d("TAG", "itemClickinsideapi: $branchListSpinner")
                        var branchList: ArrayList<String> = ArrayList()
                        for (i in branchListSpinner.indices) {
                            val branchName: String =
                                branchListSpinner[i].branchName // I want to show this when Selected
                            branchList.add(branchName)

                        }

                        val branchData = branchList
                        getViewModel()?.branches(
                            mDataBinding,
                            branchData,
                            idToken,
                            spRegId,
                            serviceCategoryId,
                            0
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

    private fun setIdTokenAndSpRegId() {
        var getSharedPreferences = requireActivity().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )
        spRegId = getSharedPreferences.getInt("spRegId", 0)
        idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
        roleId = getSharedPreferences.getInt("roleId", 0)
    }

}