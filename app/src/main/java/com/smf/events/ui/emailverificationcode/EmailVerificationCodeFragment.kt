package com.smf.events.ui.emailverificationcode

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentEmailVerificationCodeBinding


class EmailVerificationCodeFragment :
    BaseFragment<FragmentEmailVerificationCodeBinding, EmailVerificationCodeViewModel>() {

    override fun getViewModel(): EmailVerificationCodeViewModel =
        ViewModelProvider(this).get(EmailVerificationCodeViewModel::class.java)

    override fun getBindingVariable(): Int = BR.emailVerificationCodeViewModel

    override fun getContentView(): Int = R.layout.fragment_email_verification_code

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Email Verification Code Button Listener
        mDataBinding?.eMailCodesubmitBtn?.setOnClickListener {
            eMailCodeSubmitBtnClicked()
        }

    }

    // Method for Email Code Verification
    private fun eMailCodeSubmitBtnClicked() {
        var code = mDataBinding?.emailVerificationCode?.text.toString()
        getViewModel().confirmUserAttribute(code)
    }

}