package com.smf.events.ui.signin

import android.app.Application
import android.util.Log
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.options.AuthSignInOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.smf.events.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInViewModel @Inject constructor(private val signInRepository: SignInRepository, application: Application) : BaseViewModel(application) {


    fun signIn(userName: String) {
       Amplify.Auth.signIn(userName,null,{ result ->
           if (result.isSignInComplete) {

               Log.i("AuthQuickstart", "Sign in succeeded${result}")
           } else {
               Log.i("AuthQuickstart", "Sign in not complete${result}")
viewModelScope.launch {
    callBackInterface?.callBack("SignInNotCompleted")
}
           }
       },{ Log.e("AuthQuickstart", "Failed to sign in", it) })
    }

    // Getting UserDetails During SignIn
    fun getUserDetails(loginName: String) = liveData(Dispatchers.IO) {
        Log.d("TAG", "setUserDetails: $loginName")
        emit(signInRepository.getUserDetails(loginName))

    }



    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback:CallBackInterface) {
        callBackInterface = callback
    }

    interface CallBackInterface {
        fun callBack(status: String)
    }
}
