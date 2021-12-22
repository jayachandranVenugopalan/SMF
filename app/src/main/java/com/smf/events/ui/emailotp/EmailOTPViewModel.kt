package com.smf.events.ui.emailotp

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.smf.events.base.BaseViewModel
import com.smf.events.databinding.FragmentEmailOtpBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

class EmailOTPViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

    // Custom Confirm SignIn Function
    fun confirmSignIn(otp: String, mDataBinding: FragmentEmailOtpBinding) {

        Amplify.Auth.confirmSignIn(otp,
            {
                result->
                Log.i("AuthQuickstart", "Confirmed signIn: ${result.nextStep.additionalInfo}")
//                Log.i("AuthQuickstart", "Confirmed signIn: $it")
                fetchSession()
                Amplify.Auth.fetchUserAttributes(
                    { result ->
                        Log.i("AuthDemo", "User attributes = ${result.get(0).key}")
                        if (result[1].value.equals("false")) {
                            eMailVerification()

                        } else {
                            Log.i("AuthDemo", "User attributes = successfully entered dashboard")

                            viewModelScope.launch {
                                callBackInterface?.callBack("EMailVerifiedTrueGoToDashBoard")
                            }
                        }
                    },
                    {
                        Log.e("AuthDemo", "Failed to fetch user attributes", it)
                        viewModelScope.launch {
                            toastMessage = "Invalid OTP"
                            callBackInterface!!.awsErrorreponse()
                        }
                    })
            },
            {
                Log.e(
                    "AuthQuickstart",
                    "Failed to confirm signIn ${it.cause!!.message!!.split(".")[0]}",
                    it
                )
                viewModelScope.launch {
                    var errMsg = mDataBinding.otpemail.text.toString()
                    if (errMsg.isEmpty()) {
                        toastMessage = "Enter OTP"
                        callBackInterface!!.awsErrorreponse()
                    }
                }
            })
    }

    // Fetch tokens
    private fun fetchSession(){
        Amplify.Auth.fetchAuthSession(
            {
                val session = it as AWSCognitoAuthSession

                var tokens= AuthSessionResult.success(session.userPoolTokens.value!!.accessToken)
                Log.d("TAG", "fetchsession: $tokens")

            },
            { Log.e("AuthQuickStart", "Failed to fetch session", it) }
        )
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
            {
                Log.e("AuthDemo", "Failed to resend code", it)
                viewModelScope.launch {
                    var errMsg = it.cause!!.message!!.split(".")[0]
                    toastMessage = errMsg
                    callBackInterface!!.awsErrorreponse()
                }
            })
    }

    // OTP Resend SignIn Method
    fun reSendOTP(userName: String) {
        Amplify.Auth.signIn(userName, null, {
            Log.d("TAG", "reSendOTP: called code resented successfully")
        },
            {
                Log.e("AuthQuickstart", "Failed to sign in", it)
                viewModelScope.launch {
                    var errMsg = it.cause!!.message!!.split(".")[0]
                    toastMessage = errMsg
                    callBackInterface!!.awsErrorreponse()
                }
            })
    }

    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBack Interface
    interface CallBackInterface {
        fun callBack(status: String)
        fun awsErrorreponse()
    }

}