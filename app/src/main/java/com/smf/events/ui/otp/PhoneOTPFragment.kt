package com.smf.events.ui.otp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.amplifyframework.core.Amplify
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentPhoneOTPBinding


class PhoneOTPFragment : BaseFragment<FragmentPhoneOTPBinding,PhoneOTPViewModel>(){


    override fun getViewModel(): PhoneOTPViewModel? =
        ViewModelProvider(this).get(PhoneOTPViewModel::class.java)


    override fun getBindingVariable(): Int =BR.otpviewmodel

    override fun getContentView(): Int =R.layout.fragment_phone_o_t_p

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mDataBinding!!.submitBtn.setOnClickListener {
            submitBtnClicked()
        }


    }
    //For confirmSignIn aws
    private  fun submitBtnClicked(){
        var code=mDataBinding?.otpemail?.text.toString()
        Log.d("TAG", "onViewCreated: ${code}")
        getViewModel()!!.confirmSignIn(code)
    }
}