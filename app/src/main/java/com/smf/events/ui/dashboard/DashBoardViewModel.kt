package com.smf.events.ui.dashboard

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.liveData
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.smf.events.SMFApp
import com.smf.events.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DashBoardViewModel  @Inject constructor(
    private val dashBoardRepository: DashBoardRepository,
    application: Application
) : BaseViewModel(application) {

    // EventType Api
    fun get184Types(idToken: String) = liveData(Dispatchers.IO) {
        emit(dashBoardRepository.get184Types(idToken))
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
    }
}