package com.smf.events.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseActivity
import com.smf.events.databinding.FragmentDashBoardBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.helper.Tokens
import com.smf.events.ui.actionandstatusdashboard.ActionsAndStatusFragment
import com.smf.events.ui.dashboard.adapter.MyEventsAdapter
import com.smf.events.ui.dashboard.model.MyEvents
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DashBoardActivity : BaseActivity<FragmentDashBoardBinding, DashBoardViewModel>(),
    DashBoardViewModel.CallBackInterface, Tokens.IdTokenCallBackInterface {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
         restrictBackButton()
    }

    override fun onStart() {
        super.onStart()
        val allServices = ArrayList<String>()
        val branch = ArrayList<String>()
        // Initialize IdTokenCallBackInterface
        tokens.setCallBackInterface(this)

        getViewModel().setCallBackInterface(this)

        //spinner view for allservices
        getViewModel().allServices(getViewDataBinding(), allServices)
        //spinner view for branches
         //  getViewModel().branches(getViewDataBinding(), branch)

        myEventsRecyclerView = getViewDataBinding()?.eventsRecyclerView!!

        // MyEvent Recycler
        myEventsRecycler()

        val list = getList()
        adapter.refreshItems(list)


        supportFragmentManager.beginTransaction()
            .add(R.id.action_and_status_layout, ActionsAndStatusFragment())
            .setReorderingAllowed(true)
            .commit()


//        mDataBinding?.clickbtn?.setOnClickListener {
//            var getSharedPreferences = requireActivity().applicationContext.getSharedPreferences("MyUser", Context.MODE_PRIVATE)
//            var idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
//            clickBtn(idToken)
//        }
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
            LinearLayoutManager(this@DashBoardActivity, LinearLayoutManager.HORIZONTAL, false)
        myEventsRecyclerView.adapter = adapter
    }


    override suspend fun tokenCallBack(idToken: String, caller: String) {
        Log.d("TAG", "check clickBtn dashboard after ")
        withContext(Dispatchers.Main) {
            getViewModel().get184Types(idToken)
                .observe(this@DashBoardActivity, Observer { apiResponse ->
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

    // Method for restrict user back button
    private fun restrictBackButton() {
        this@DashBoardActivity.onBackPressedDispatcher.addCallback(this) {
            if (pressedTime + 1500 > System.currentTimeMillis()) {
                this@DashBoardActivity.finish()
            } else {
                showToast(getString(R.string.Press_back_again_to_exit))

            }
            pressedTime = System.currentTimeMillis()
        }
    }

    override fun callBack(idToken: String) {
        Log.d("AuthQuickStart", "Dashboard callBack called token $idToken")
        CoroutineScope(Dispatchers.Main).launch {
            getViewModel().get184Types(idToken)
                .observe(this@DashBoardActivity, Observer { apiResponse ->
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
                this@DashBoardActivity,
                resources.getStringArray(R.array.all_services)[position],
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    override fun branchItemClick(position: Int, name: String?, allServiceposition: Int?) {

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


}