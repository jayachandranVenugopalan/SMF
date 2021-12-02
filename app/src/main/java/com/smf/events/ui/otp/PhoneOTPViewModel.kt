package com.smf.events.ui.otp

import android.app.Application
import android.util.Log
import com.amplifyframework.core.Amplify
import com.smf.events.base.BaseViewModel
import javax.inject.Inject

class PhoneOTPViewModel @Inject constructor(application: Application) :BaseViewModel(application) {

    fun confirmSignIn(otp: String?) {
        if (otp != null) {
            Log.d("AuthQuickstart", "confirmSignUp:$otp ")
            Amplify.Auth.confirmSignIn(otp,
                { Log.i("AuthQuickstart", "Confirmed signin: $it") },
                { Log.e("AuthQuickstart", "Failed to confirm signin", it) }
            )
        }


    }
}