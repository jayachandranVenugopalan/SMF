package com.smf.events.ui.signup

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.SignUpFragmentBinding

class SignUpFragment : BaseFragment<SignUpFragmentBinding,SignUpViewModel>() {
    override fun getViewModel(): SignUpViewModel? =ViewModelProvider(this).get(SignUpViewModel::class.java)

    override fun getBindingVariable():Int=BR.signupViewModel

    override fun getContentView(): Int =R.layout.sign_up_fragment


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    //tabLayout for service and event in Ui
        setTabLayout()

        mDataBinding!!.submitSignup.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToEmailOTPFragment())
        }
    }


    private fun setTabLayout(){
        var tabLayout= mDataBinding?.tabLayout

        tabLayout!!.addTab(tabLayout.newTab().setText("Event organize"))
        tabLayout.addTab(tabLayout.newTab().setText("Service provider"))
        tabLayout.tabGravity= TabLayout.GRAVITY_FILL
    }
}