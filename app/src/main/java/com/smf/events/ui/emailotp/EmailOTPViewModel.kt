package com.smf.events.ui.emailotp

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.amazonaws.mobile.client.AWSMobileClient
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.smf.events.SMFApp
import com.smf.events.base.BaseViewModel
import com.smf.events.databinding.FragmentEmailOtpBinding
import com.smf.events.helper.Tokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmailOTPViewModel @Inject constructor(
    private val eMailOTPRepository: EmailOTPRepository,
    application: Application
) : BaseViewModel(application) {

    // Custom Confirm SignIn Function
    fun confirmSignIn(otp: String, mDataBinding: FragmentEmailOtpBinding) {

        Amplify.Auth.confirmSignIn(otp,
            { result ->
                Log.i("AuthQuickstart", "Confirmed signIn: ${result.nextStep.additionalInfo}")
                rememberDevice()
                fetchSession()
               Amplify.Auth.fetchUserAttributes(
                    { result ->
                        Log.i("AuthDemo", "User attributes = $result")
                        Log.i("AuthDemo", "User attributes = ${result[0].key}")
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

    private fun rememberDevice() {

      Amplify.Auth.rememberDevice(
            {
                Log.i("AuthQuickStart", "Remember device succeeded")
            },
            { Log.e("AuthQuickStart", "Remember device failed with error", it) }
        )
    }


    // Fetch tokens
    private fun fetchSession() {
        Amplify.Auth.fetchAuthSession(
            {
                val session = it as AWSCognitoAuthSession
                Log.i("AuthDemo", "User attributes = $session")
                var idToken =
                    AuthSessionResult.success(session.userPoolTokens.value!!.idToken).value
                Log.i("AuthDemo", "idToken = $idToken")
                setToken(idToken!!)
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

    // Method For Getting Service Provider Reg Id and Role Id
    fun getLoginInfo(idToken: String) = liveData(Dispatchers.IO) {
        emit(eMailOTPRepository.getLoginInfo(idToken))
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
        suspend fun callBack(status: String)
        fun awsErrorreponse()
    }

}