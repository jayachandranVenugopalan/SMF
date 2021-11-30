package com.smf.events.ui.verificationcode

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.amplifyframework.core.Amplify
import com.smf.events.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class VerificationCodeViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

     fun confirmSignUpFunctionality(userName: String, code: String){

            Amplify.Auth.confirmSignUp(
                userName, code,
                { result ->
                    var status :String? = null
                    status = if (result.isSignUpComplete) {
                        Log.i("AuthQuickstart", "Confirm signUp succeeded")
                        "Confirm signUp succeeded"
                    } else {
                        Log.i("AuthQuickstart","Confirm sign up not complete")
                        "Confirm sign up not complete"
                    }
                    viewModelScope.launch {
                        callBackInterface?.callBack(status,"SignUpCompleted" )
                    }
                },
                {
                    Log.e("AuthQuickstart", "Failed to confirm sign up", it)
                    viewModelScope.launch {
                        callBackInterface?.callBack("Failed to confirm sign up", "SignUpFailed")
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
         fun callBack(status : String,flag : String)
    }

}