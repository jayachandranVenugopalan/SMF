package com.smf.events.ui.signup

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.smf.events.base.BaseViewModel
import com.smf.events.ui.verificationcode.VerificationCodeViewModel
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : BaseViewModel(application) {


    fun signUp(userName: String, password: String, options: AuthSignUpOptions){

        Amplify.Auth.signUp(
            userName, password, options,
            { Log.i("AuthQuickstart", "Sign up result = $it")
                viewModelScope.launch {
                    callBackInterface?.callBack("signUpResult")
                }
            },
            { Log.i("AuthQuickstart", "Sign up failed", it)
                viewModelScope.launch {
                    callBackInterface?.callBack("signUpFailed")
                }

            }
        )
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