package com.smf.events.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.core.Amplify
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.SMFApp
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentDashBoardBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.helper.Tokens
import com.smf.events.ui.dashboard.adapter.MyEventsAdapter
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.concurrent.ForkJoinTask.getPool
import javax.inject.Inject

class DashBoardFragment : BaseFragment<FragmentDashBoardBinding, DashBoardViewModel>(),DashBoardViewModel.CallBackInterface
    ,Tokens.IdTokenCallBackInterface {

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
    private var pressedTime :Long = 0

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restrictBackButton()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize IdTokenCallBackInterface
        tokens.setCallBackInterface(this)
        getViewModel().setCallBackInterface(this)

        myEventsRecyclerView = mDataBinding?.eventsRecyclerView!!
        // MyEvent Recycler
        myEventsRecycler()

        mDataBinding?.clickbtn?.setOnClickListener {
            var getSharedPreferences = requireActivity().applicationContext.getSharedPreferences("MyUser", Context.MODE_PRIVATE)
            var idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
            clickBtn(idToken)
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
            getViewModel().get184Types(idToken).observe(this@DashBoardFragment, Observer { apiResponse ->
                when(apiResponse){
                    is ApisResponse.Success ->{
                        Log.d("TAG", "token 184 result: ${apiResponse.response.success}")
                        Log.d("TAG", "token 184 result: ${apiResponse.response.result.info}")
                        showToast("ok")
                    }
                    is ApisResponse.Error ->{
                        Log.d("TAG", "token 184 result: ${apiResponse.exception}")
                        showToast("Not ok")
                    }
                }
            })
        }
    }

    // Method for restrict user back button
    private fun restrictBackButton(){
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (pressedTime + 1500 > System.currentTimeMillis()) {
                requireActivity().finish()
            }
            else {
                showToast(getString(R.string.Press_back_again_to_exit))
            }
            pressedTime = System.currentTimeMillis()
        }
    }

    override fun callBack(idToken: String) {
        Log.d("AuthQuickStart", "Dashboard callBack called token $idToken")
        CoroutineScope(Main).launch {
            getViewModel().get184Types(idToken).observe(this@DashBoardFragment, Observer { apiResponse ->
                when(apiResponse){
                    is ApisResponse.Success ->{
                        Log.d("TAG", "token 184 result: ${apiResponse.response.success}")
                        Log.d("TAG", "token 184 result: ${apiResponse.response.result.info}")
                        showToast("ok")
                    }
                    is ApisResponse.Error ->{
                        Log.d("TAG", "token 184 result: ${apiResponse.exception}")
                        showToast("Not ok")
                    }
                }
            })
        }

    }
}