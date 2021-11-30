package com.smf.events.ui.signup

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.google.android.material.tabs.TabLayout
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.SignUpFragmentBinding
import okhttp3.internal.Util.concat

class SignUpFragment : BaseFragment<SignUpFragmentBinding, SignUpViewModel>(), SignUpViewModel.CallBackInterface {

    private lateinit var userName: String
    private lateinit var password: String

    override fun getViewModel(): SignUpViewModel? =
        ViewModelProvider(this).get(SignUpViewModel::class.java)

    override fun getBindingVariable(): Int = BR.signupViewModel

    override fun getContentView(): Int = R.layout.sign_up_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize CallBackInterface
        getViewModel()?.setCallBackInterface(this)

        //tabLayout for service and event in Ui
        setTabLayout()

        mDataBinding!!.submitSignup.setOnClickListener {
            signUpFunctionality()
        }
    }

    // Method for SignUp Function
    private fun signUpFunctionality() {

        var firstName = mDataBinding?.editTextFirstName?.text.toString()
        var lastName = mDataBinding?.editTextLastName?.text.toString()
        var eMail = mDataBinding?.editTextEmail?.text.toString()
        var countryCode = mDataBinding?.cpp?.selectedCountryCode
        var phoneNumber = mDataBinding?.editTextNumber?.text

        val options = AuthSignUpOptions.builder()
            .userAttribute(
                AuthUserAttributeKey.name(),
                firstName.plus(" ").plus(lastName)
            )
            .userAttribute(
                AuthUserAttributeKey.phoneNumber(),
                "+".plus(countryCode).plus(phoneNumber)
            )
            .userAttribute(AuthUserAttributeKey.email(), eMail)
            .build()

        userName= lastName.plus(firstName)
        password = "Service@123"

        getViewModel()?.signUp( userName,password , options)
    }


    private fun setTabLayout() {
        mDataBinding!!.tabLayout.addTab(mDataBinding!!.tabLayout.newTab().setText("Event organize"))
        mDataBinding!!.tabLayout.addTab(
            mDataBinding!!.tabLayout.newTab().setText("Service provider")
        )
        mDataBinding!!.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
    }

    override fun callBack(status: String) {

        if (status=="signUpResult"){
            val action = SignUpFragmentDirections.actionSignUpFragmentToVerificationCodeFrgment(userName)
            findNavController().navigate(action)
        }else{
            Toast.makeText(requireContext(), "Param Details Not Correct", Toast.LENGTH_SHORT).show()
        }
    }

}