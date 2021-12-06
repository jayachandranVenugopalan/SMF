package com.smf.events.ui.signin

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.SignInFragmentBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.ui.signup.model.GetUserDetails
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SignInFragment : BaseFragment<SignInFragmentBinding, SignInViewModel>(),
    SignInViewModel.CallBackInterface {

    private lateinit var mobileNumberWithCountryCode: String
    private lateinit var eMail: String

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): SignInViewModel? =
        ViewModelProvider(this, factory).get(SignInViewModel::class.java)

    override fun getBindingVariable(): Int = BR.signinViewModel

    override fun getContentView(): Int = R.layout.sign_in_fragment

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize CallBackInterface
        getViewModel()?.setCallBackInterface(this)

        // SignIn Button Listener
        mDataBinding!!.signinbtn.setOnClickListener {
            signInClicked()

        }

        // SignUp Button Listener
        mDataBinding!!.signupaccbtn.setOnClickListener {
            onSignupclicked()
        }

    }

    // Method for SignIn Button
    private fun signInClicked() {
        var phoneNumber = mDataBinding?.editTextMobileNumber?.text.toString().trim()
        var countryCode = mDataBinding?.cppSignIn?.selectedCountryCode
        mobileNumberWithCountryCode = "+".plus(countryCode).plus(phoneNumber)
        eMail = mDataBinding?.editTextEmail?.text.toString()

        if (phoneNumber.isNotEmpty() || eMail.isNotEmpty()) {
            if (phoneNumber.isEmpty() || eMail.isEmpty()) {
                if (phoneNumber.isEmpty()) {
                    getViewModel()?.getUserDetails(eMail)
                        ?.observe(viewLifecycleOwner, getUserDetailsObserver)
                } else {
                    getViewModel()?.getUserDetails(mobileNumberWithCountryCode)
                        ?.observe(viewLifecycleOwner, getUserDetailsObserver)
                }
            } else {
                showToast("Please Enter Any EMail or Phone Number")
            }
        } else {
            showToast("Please Enter Email or MobileNumber")
        }
    }

    // Observing GetUserDetails Api Call
    private val getUserDetailsObserver = Observer<ApisResponse<GetUserDetails>> { apiResponse ->
        when (apiResponse) {
            is ApisResponse.Success -> {
                getViewModel()?.signIn(apiResponse.response.data.userName)
            }

            is ApisResponse.Error -> {
                showToast(apiResponse.exception.localizedMessage!!)
                Log.d("TAG", "response: failure ${apiResponse.exception.localizedMessage!!}")
            }
            else -> {}
        }
    }

    // Method for SignUp Button
    private fun onSignupclicked() {
        var action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
        // Navigate to SignUpFragment
        findNavController().navigate(action)
    }

    // CallBackInterface Override Method
    override fun callBack(status: String) {
        if (status == "SignInNotCompleted") {
            // Navigate to EmailOTPFragment
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToEMailOTPFragment())
        } else if (status == "signInCompletedGoToDashBoard") {
            //Navigate to DashBoardFragment
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToDashBoardFragment())
        }
    }
}