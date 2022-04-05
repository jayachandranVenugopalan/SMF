package com.smf.events.ui.emailotp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentEmailOtpBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.ui.emailotp.model.GetLoginInfo
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.*
import javax.inject.Inject


class EmailOTPFragment : BaseFragment<FragmentEmailOtpBinding, EmailOTPViewModel>(),
    EmailOTPViewModel.CallBackInterface {

    private val args: EmailOTPFragmentArgs by navArgs()
    private lateinit var userName: String
    private lateinit var getSharedPreferences: SharedPreferences

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): EmailOTPViewModel =
        ViewModelProvider(this, factory).get(EmailOTPViewModel::class.java)

    override fun getBindingVariable(): Int = BR.otpviewmodel

    override fun getContentView(): Int = R.layout.fragment_email_otp

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize Local Variables
        setUserNameAndSharedPref()
        // Initialize CallBackInterface
        getViewModel().setCallBackInterface(this)
        // Submit Button Listener
        submitBtnClicked()
        // Resend Text Listener
        resendBtnClicked()
        //Auto suubmit pin
        autoSubmit()
    }

    // Method For set UserName And SharedPreferences
    private fun setUserNameAndSharedPref(){
        userName = args.userName
        getSharedPreferences= requireActivity().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )
    }

    //For confirmSignIn aws
    private fun submitBtnClicked() {
        val code = mDataBinding?.otpemail?.text.toString()
        mDataBinding!!.submitBtn.setOnClickListener {
            getViewModel().confirmSignIn(code, mDataBinding!!)
        }

    }

    // Method for ResendingOTP
    private fun resendBtnClicked() {
        mDataBinding?.otpResend?.setOnClickListener {
            getViewModel().reSendOTP(userName)
        }
    }

    //AutoSubmitted when we Enter 4 digit
    private fun autoSubmit() {
        getViewModel().userOtpNumber.observe(viewLifecycleOwner, Observer {
            if (it.length == 4) {
                mDataBinding?.submitBtn?.visibility = View.INVISIBLE
                getViewModel().confirmSignIn(it, mDataBinding!!)
            } else {
                mDataBinding?.submitBtn?.visibility = View.VISIBLE
            }
        })
    }

    // CallBackInterface Override Method
    override suspend fun callBack(status: String) {
        if (status == "goToEmailVerificationCodePage") {
            // Navigate to EmailVerificationCodeFragment
            findNavController().navigate(EmailOTPFragmentDirections.actionPhoneOTPFragmentToEmailVerificationCodeFragment())
        } else if (status == "EMailVerifiedTrueGoToDashBoard") {
            val idToken = "Bearer ${getSharedPreferences.getString("IdToken", "")}"
            getLoginApiCall(idToken)
        }
    }

    //AWS Error response
    override fun awsErrorResponse() {
        showToast(getViewModel().toastMessage)
    }

    //Login api call to Fetch RollId and SpRegId
    private fun getLoginApiCall(idToken: String) {
        // Getting Service Provider Reg Id and Role Id
        getViewModel().getLoginInfo(idToken)
            .observe(this@EmailOTPFragment, Observer { apiResponse ->
                when (apiResponse) {
                    is ApisResponse.Success -> {
                        // Initialize RegId And RoleId to Shared Preference
                        setSpRegIdAndRollID(apiResponse)
                        // Navigate to DashBoardFragment
                        findNavController().navigate(EmailOTPFragmentDirections.actionEMailOTPFragmentToDashBoardFragment())
                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })
    }

    // Method For Set SpRegId And RollID to SharedPreference From Login Api
    private fun setSpRegIdAndRollID(apiResponse: ApisResponse.Success<GetLoginInfo>) {
        val editor: SharedPreferences.Editor = getSharedPreferences.edit()
        editor.putInt(
            "spRegId",
            apiResponse.response.data.spRegId
        )
        editor.putInt(
            "roleId",
            apiResponse.response.data.roleId
        )
        editor.apply()
    }
}
