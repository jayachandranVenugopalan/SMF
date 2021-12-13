package com.smf.events.ui.emailotp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentEmailOtpBinding


class EmailOTPFragment : BaseFragment<FragmentEmailOtpBinding, EmailOTPViewModel>(),
    EmailOTPViewModel.CallBackInterface {

    private val args: EmailOTPFragmentArgs by navArgs()
    private lateinit var userName: String

    override fun getViewModel(): EmailOTPViewModel? =
        ViewModelProvider(this).get(EmailOTPViewModel::class.java)

    override fun getBindingVariable(): Int = BR.otpviewmodel

    override fun getContentView(): Int = R.layout.fragment_email_otp

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userName = args.userName
        // Initialize CallBackInterface
        getViewModel()?.setCallBackInterface(this)

        // Submit Button Listener
        mDataBinding!!.submitBtn.setOnClickListener {
            submitBtnClicked()
        }

        // Resend Text Listener
        mDataBinding?.otpResend?.setOnClickListener {
            resendBtnClicked()
        }

    }

    //For confirmSignIn aws
    private fun submitBtnClicked() {
        var code = mDataBinding?.otpemail?.text.toString()
        Log.d("TAG", "onViewCreated: ${code}")
        getViewModel()!!.confirmSignIn(code, mDataBinding!!)
    }

    // Method for ResendingOTP
    private fun resendBtnClicked() {
        getViewModel()?.reSendOTP(userName)
    }

    // CallBackInterface Override Method
    override fun callBack(status: String) {
        if (status == "goToEmailVerificationCodePage") {
            // Navigate to EmailVerificationCodeFragment
            findNavController().navigate(EmailOTPFragmentDirections.actionPhoneOTPFragmentToEmailVerificationCodeFragment())
        } else if (status == "EMailVerifiedTrueGoToDashBoard") {
            // Navigate to DashBoardFragment
            findNavController().navigate(EmailOTPFragmentDirections.actionEMailOTPFragmentToDashBoardFragment())
        }
    }

    override fun awsErrorreponse() {
        getViewModel()?.toastMessage?.let { showToast(it) }
    }
}