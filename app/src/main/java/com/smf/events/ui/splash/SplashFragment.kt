package com.smf.events.ui.splash

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.SplashScreenFragmentBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SplashFragment : BaseFragment<SplashScreenFragmentBinding,SplashScreenViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): SplashScreenViewModel? =
        ViewModelProvider(this,factory).get(SplashScreenViewModel::class.java)

    override fun getBindingVariable(): Int =BR.splashViewModel

    override fun getContentView(): Int =R.layout.splash_screen_fragment

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Login Button Listener
        onClickLoginBtn()

    }
    private fun onClickLoginBtn(){
        mDataBinding!!.splashBtn.setOnClickListener {
            // Getting User Inputs
var action=SplashFragmentDirections.actionSplashFragmentToSignInFragment()

            findNavController().navigate(action)
        }
    }
}