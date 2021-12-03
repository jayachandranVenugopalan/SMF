package com.smf.events.ui.emailotp

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.smf.events.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class EmailOTPViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

    fun confirmSignIn(otp: String) {

        // Custom Confirm SignIn Function
        Amplify.Auth.confirmSignIn(otp,
            {
                Log.i("AuthQuickstart", "Confirmed signIn: $it")
                Amplify.Auth.fetchUserAttributes(
                    { result ->
                        Log.i("AuthDemo", "User attributes = $result")
                        if (result[1].value.equals("false")) {
                            eMailVerification()
                        } else {
                            Log.i("AuthDemo", "User attributes = successfully entered dashboard")
                        }
                    },
                    { Log.e("AuthDemo", "Failed to fetch user attributes", it) })
            },
            { Log.e("AuthQuickstart", "Failed to confirm signIn", it) })

    }

    // Email Verification
    private fun eMailVerification() {
        Amplify.Auth.resendUserAttributeConfirmationCode(AuthUserAttributeKey.email(),
            {
                Log.i("AuthDemo", "Code was sent again: $it")
                viewModelScope.launch {
                    callBackInterface?.callBack("goToEmailVerificationCodePage")
                }
            },
            { Log.e("AuthDemo", "Failed to resend code", it) })
    }


    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBack Interface
    interface CallBackInterface {
        fun callBack(status: String)
    }

}