package com.smf.events.ui.signin

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.amplifyframework.core.Amplify
import com.smf.events.base.BaseViewModel
import javax.inject.Inject

class SignInViewModel @Inject constructor(application: Application) : BaseViewModel(application) {


    fun signIn(){
       // Amplify.Auth.signIn()
    }
}
