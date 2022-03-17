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
import com.smf.events.SMFApp
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentEmailOtpBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.helper.Tokens
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject


class EmailOTPFragment : BaseFragment<FragmentEmailOtpBinding, EmailOTPViewModel>(),
    EmailOTPViewModel.CallBackInterface, Tokens.IdTokenCallBackInterface {

    private val args: EmailOTPFragmentArgs by navArgs()
    private lateinit var userName: String

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var tokens: Tokens

    override fun getViewModel(): EmailOTPViewModel? =
        ViewModelProvider(this, factory).get(EmailOTPViewModel::class.java)

    override fun getBindingVariable(): Int = BR.otpviewmodel

    override fun getContentView(): Int = R.layout.fragment_email_otp

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userName = args.userName
        // Initialize CallBackInterface
        getViewModel()?.setCallBackInterface(this)
        // Initialize IdTokenCallBackInterface
        tokens.setCallBackInterface(this)

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
    override suspend fun callBack(status: String) {
        if (status == "goToEmailVerificationCodePage") {
            // Navigate to EmailVerificationCodeFragment
            findNavController().navigate(EmailOTPFragmentDirections.actionPhoneOTPFragmentToEmailVerificationCodeFragment())
        } else if (status == "EMailVerifiedTrueGoToDashBoard") {

            val getSharedPreferences = requireActivity().applicationContext.getSharedPreferences(
                "MyUser",
                Context.MODE_PRIVATE
            )
            val idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"

            withContext(Main) {
                if (idToken.isNotEmpty()) {
                    tokens.checkTokenExpiry(
                        requireActivity().applicationContext as SMFApp,
                        "event_type"
                    )
                }
            }
        }
    }

    override fun awsErrorreponse() {
        getViewModel()?.toastMessage?.let { showToast(it) }
    }

    //Override Method for IdTokenCallBackInterface
    override suspend fun tokenCallBack(idToken: String, caller: String) {
        Log.d("TAG", "check tokenCallBack called......................$caller")
        withContext(Main) {

            // Getting Service Provider Reg Id and Role Id
            getViewModel()?.getLoginInfo(idToken)
                ?.observe(this@EmailOTPFragment, Observer { apiResponse ->
                    when (apiResponse) {
                        is ApisResponse.Success -> {
                            // Initialize RegId And RoleId to Shared Preference
                            val sharedPreferences =
                                requireContext().getSharedPreferences(
                                    "MyUser",
                                    Context.MODE_PRIVATE
                                )
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putInt(
                                "spRegId",
                                apiResponse.response.data.spRegId
                            )
                            editor.putInt(
                                "roleId",
                                apiResponse.response.data.roleId
                            )
                            editor.apply()
                            findNavController().navigate(EmailOTPFragmentDirections.actionEMailOTPFragmentToDashBoardFragment())
                        }
                        is ApisResponse.Error -> {
                            Log.d("TAG", "check token result: ${apiResponse.exception}")
                        }
                        else -> {}
                    }
                })
        }

    }




}
