package com.smf.events.ui.emailverificationcode

import android.app.Application
import android.util.Log
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.smf.events.base.BaseViewModel
import javax.inject.Inject

class EmailVerificationCodeViewModel @Inject constructor(application: Application) :
    BaseViewModel(application) {

    fun confirmUserAttribute(code: String) {
        // ConfirmUser
        Amplify.Auth.confirmUserAttribute(
            AuthUserAttributeKey.email(), code,
            {
                Log.i("AuthDemo", "Confirmed user attribute with correct code.")

            },
            { Log.e("AuthDemo", "Failed to confirm user attribute. Bad code?", it) }
        )
    }


}