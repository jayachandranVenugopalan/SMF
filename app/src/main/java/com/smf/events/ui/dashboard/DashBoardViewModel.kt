package com.smf.events.ui.dashboard

import android.R
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.liveData
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.smf.events.SMFApp
import com.smf.events.base.BaseViewModel
import com.smf.events.databinding.FragmentDashBoardBinding
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DashBoardViewModel  @Inject constructor(
    private val dashBoardRepository: DashBoardRepository,
    application: Application
) : BaseViewModel(application) ,AdapterView.OnItemSelectedListener{

    // EventType Api
    fun get184Types(idToken: String) = liveData(Dispatchers.IO) {
        emit(dashBoardRepository.get184Types(idToken))
    }

@SuppressLint("ResourceType")
fun allServices(mDataBinding: FragmentDashBoardBinding?, resources: Array<String>) {

    val spin = mDataBinding!!.spnAllServices
    spin.onItemSelectedListener = this

    val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(getApplication(),R.layout.simple_spinner_item,resources)
    // set simple layout resource file
    // for each item of spinner
    ad.setDropDownViewResource(
        android.R.layout.simple_spinner_dropdown_item)

    // Set the ArrayAdapter (ad) data on the
    // Spinner which binds data to spinner
    spin.adapter = ad
}
    @SuppressLint("ResourceType")
    fun branches(mDataBinding: FragmentDashBoardBinding?, resources: Array<String>) {

        val spin = mDataBinding!!.spnBranches
        spin.onItemSelectedListener = this

        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(getApplication(),R.layout.simple_spinner_item,resources)
        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spin.adapter = ad
    }
    // Fetch tokens
      fun fetchSession() {
        Amplify.Auth.fetchAuthSession(
            {
                val session = it as AWSCognitoAuthSession
                var idToken =
                    AuthSessionResult.success(session.userPoolTokens.value!!.idToken).value
                Log.d("AuthQuickStart", "Dashboard view model fetch token $idToken")
                setToken(idToken!!)
                callBackInterface?.callBack("Bearer $idToken")
            },
            { Log.e("AuthQuickStart", "Failed to fetch session", it) }
        )
    }

    // Method for save IdToken
    private fun setToken(token: String) {
        var sharedPreferences =
            getApplication<SMFApp>().getSharedPreferences("MyUser", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()
        editor?.putString("IdToken", token)
        editor?.apply()
    }

    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBack Interface
    interface CallBackInterface {
        fun callBack(token: String)
        fun itemClick(msg:Int)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        callBackInterface?.itemClick(position)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}