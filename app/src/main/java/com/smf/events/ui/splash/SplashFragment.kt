package com.smf.events.ui.splash

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.SplashScreenFragmentBinding
import com.smf.events.ui.dashboard.DashBoardActivity
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SplashFragment : BaseFragment<SplashScreenFragmentBinding, SplashScreenViewModel>() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): SplashScreenViewModel =
        ViewModelProvider(this, factory).get(SplashScreenViewModel::class.java)

    override fun getBindingVariable(): Int = BR.splashViewModel

    override fun getContentView(): Int = R.layout.splash_screen_fragment

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val getSharedPreferences = requireActivity().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )
        val idToken = getSharedPreferences?.getString("IdToken", "")
        if (!idToken.isNullOrEmpty()) {
            moveToDashBoardScreen()
        } else {
            // Login Button Listener
            onClickLoginBtn()
        }

    }

    private fun onClickLoginBtn() {
        mDataBinding!!.splashBtn.setOnClickListener {
            // Getting User Inputs
            var action = SplashFragmentDirections.actionSplashFragmentToSignInFragment()
           // val action = SplashFragmentDirections.actionSplashFragmentToBusinessRegistrationFragment()
            findNavController().navigate(action)
        }
    }

    // Method for Moving DashBoard Screen
    private fun moveToDashBoardScreen() {

//        startActivity(Intent(requireContext(), DashBoardActivity::class.java))
//        requireActivity().finish()
        val action = SplashFragmentDirections.actionSplashFragmentToDashBoardFragment()

        findNavController().navigate(action)
    }
}