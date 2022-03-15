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
import com.smf.events.ui.actionandstatusdashboard.ActionsAndStatusFragment
import com.smf.events.ui.dashboard.adapter.MyEventsAdapter
import com.smf.events.ui.dashboard.model.MyEvents
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DashBoardFragment : BaseFragment<FragmentDashBoardBinding, DashBoardViewModel>(),
    DashBoardViewModel.CallBackInterface, Tokens.IdTokenCallBackInterface {
     var spRedId:Int=0
    lateinit var idToken:String
    var roleId:Int=0
    private lateinit var myEventsRecyclerView: RecyclerView
    lateinit var adapter: MyEventsAdapter

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
        restrictBackButton()
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var allServices = resources.getStringArray(R.array.all_services)
        var branch = resources.getStringArray(R.array.branches)
        // Initialize IdTokenCallBackInterface
        tokens.setCallBackInterface(this)

        getViewModel().setCallBackInterface(this)

        var getSharedPreferences = requireActivity().applicationContext.getSharedPreferences("MyUser", Context.MODE_PRIVATE)
        Log.d("TAG", "onViewCreated spRegId: ${getSharedPreferences.getString("spRegId","")}")
        Log.d("TAG", "onViewCreated idtoken: ${getSharedPreferences.getString("IdToken","")}")
        Log.d("TAG", "onViewCreated roleId: ${getSharedPreferences.getString("roleId","")}")
        spRedId= getSharedPreferences.getString("spRegId","")!!.toInt()
        idToken="Bearer ${getSharedPreferences?.getString("IdToken", "")}"
        roleId=getSharedPreferences.getString("roleId","")!!.toInt()






        //spinner view for allservices
        getViewModel().allServices(mDataBinding, allServices)
        //spinner view for branches
        getViewModel().branches(mDataBinding, branch)

        myEventsRecyclerView = mDataBinding?.eventsRecyclerView!!

        // MyEvent Recycler
        myEventsRecycler()

        val list = getList()
        adapter.refreshItems(list)

        actionAndStatusFragment()


//        mDataBinding?.clickbtn?.setOnClickListener {
//            var getSharedPreferences = requireActivity().applicationContext.getSharedPreferences("MyUser", Context.MODE_PRIVATE)
//            var idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
//            clickBtn(idToken)
//        }




    }

    override fun onResume() {
        super.onResume()

        if (idToken.isNotEmpty()) {
            tokens.checkTokenExpiry(
                requireActivity().applicationContext as SMFApp,
                "event_type" )
        }
    }


    private fun clickBtn(idToken: String) {
        Log.d("TAG", "check clickBtn dashboard before $idToken")

//         CoroutineScope(Dispatchers.IO).launch {
//             if (idToken.isNotEmpty()){
//                 tokens.checkTokenExpiry(requireActivity().applicationContext as SMFApp, "184_type")
//             }
//         }

        CoroutineScope(Dispatchers.IO).launch {
            getViewModel().fetchSession()
        }


    }

    private fun myEventsRecycler() {
        adapter = MyEventsAdapter()
        myEventsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        myEventsRecyclerView.adapter = adapter
    }

    override suspend fun tokenCallBack(idToken: String, caller: String) {
        Log.d("TAG", "check clickBtn dashboard after ")
        withContext(Main) {
//            getViewModel().get184Types(idToken)
//                .observe(this@DashBoardFragment, Observer { apiResponse ->
//                    when (apiResponse) {
//                        is ApisResponse.Success -> {
//                            Log.d("TAG", "token 184 result: ${apiResponse.response.success}")
//                            Log.d("TAG", "token 184 result: ${apiResponse.response.result.info}")
//                            showToast("ok")
//                        }
//                        is ApisResponse.Error -> {
//                            Log.d("TAG", "token 184 result: ${apiResponse.exception}")
//                            showToast("Not ok")
//                        }
//                    }
//
//
//                })
            sample(idToken, spRedId,roleId)
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

    override fun itemClick(position: Int) {

        if (position == 0) {

        } else {

            Toast.makeText(
                requireContext(),
                resources.getStringArray(R.array.all_services)[position],
                Toast.LENGTH_LONG
            )
                .show()
        }
              getBranches()

    }

    private fun getList(): ArrayList<MyEvents> {
        var list = ArrayList<MyEvents>()
        list.add(MyEvents("4", "Active"))
        list.add(MyEvents("2", "Pending"))
        list.add(MyEvents("1", "Draft"))
        list.add(MyEvents("2", "Rejected"))
        list.add(MyEvents("1", "Draft"))

        return list

    }

    // Action And Status UI setUp
    private fun actionAndStatusFragment(){
        requireActivity().supportFragmentManager.beginTransaction()
            .add(R.id.action_and_status_layout, ActionsAndStatusFragment())
            .setReorderingAllowed(true)
            .commit()
    }

    fun sample(idToken: String, spRegId: Int, roleId: Int) {

        // Getting Service Provider Reg Id and Role Id
        getViewModel().getServiceCount(idToken,spRegId)
            .observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {
                        Log.d("TAG", "sample: ${apiResponse.response.success}")
                      //  Log.d("TAG", "sample: ${apiResponse.response.data.activeServiceCount}")

                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {}
                }
            })

        // Getting All Service
        getViewModel().getAllServices(idToken,spRegId)
            .observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {
                        Log.d("TAG", "sample: ${apiResponse.response.success}")
                        Log.d("TAG", "sample: ${apiResponse.response.data}")

                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {}
                }
            })

    }

    fun getActionAndStatus(IdToken: String, spRedId: Int, i: Int, serviceVendorOnboardingId: Int) {
        getViewModel().getActionAndStatus(IdToken, spRedId,288,serviceVendorOnboardingId)
            .observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {
                        Log.d("TAG", "sample: ${apiResponse.response.success}")
                        Log.d("TAG", "sample: ${apiResponse.response}")

                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {}
                }
            })

    }

    fun getBranches(){
        getViewModel().getServicesBranches(idToken,spRedId,288)
            .observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {
                        Log.d("TAG", "sample: ${apiResponse.response.success}")
                        //  Log.d("TAG", "sample: ${apiResponse.response.datas.serviceVendorOnboardingId}")
                        Log.d("TAG", "sample: ${apiResponse.response.datas[0].branchName}")
                        var serviceVendorOnboardingId:Int?=apiResponse.response.datas[0].serviceVendorOnboardingId.toInt()
                        getActionAndStatus(idToken,spRedId,288,serviceVendorOnboardingId!!)

                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {}
                }
            })

    }


}