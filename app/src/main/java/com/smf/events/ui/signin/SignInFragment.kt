package com.smf.events.ui.signin

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.amazonaws.mobileconnectors.cognitoauth.Auth
import com.amplifyframework.core.Amplify
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.SignInFragmentBinding
import com.smf.events.databinding.SignUpFragmentBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SignInFragment : BaseFragment<SignInFragmentBinding,SignInViewModel>() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): SignInViewModel? =ViewModelProvider(this,factory).get(SignInViewModel::class.java)

    override fun getBindingVariable(): Int =BR.signinViewModel

    override fun getContentView(): Int =R.layout.sign_in_fragment

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SignIn Button Listener
        mDataBinding!!.signinbtn.setOnClickListener {
            signInClicked()
        }

       // SignUp Button Listener
        mDataBinding!!.signupaccbtn.setOnClickListener {
            onSignupclicked()
        }

    }

    private fun signInClicked(){
        getViewModel()?.signIn()
    }

    private fun onSignupclicked() {
        var action=SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        findNavController().navigate(action)
    }
}