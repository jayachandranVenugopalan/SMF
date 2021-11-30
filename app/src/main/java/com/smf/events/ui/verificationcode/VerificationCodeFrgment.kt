package com.smf.events.ui.verificationcode

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentVerificationCodeFrgmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerificationCodeFrgment :
    BaseFragment<FragmentVerificationCodeFrgmentBinding, VerificationCodeViewModel>(), VerificationCodeViewModel.CallBackInterface {

    lateinit var userName: String
    private val args: VerificationCodeFrgmentArgs by navArgs()

    override fun getViewModel(): VerificationCodeViewModel? =
        ViewModelProvider(this).get(VerificationCodeViewModel::class.java)

    override fun getBindingVariable(): Int = BR.verificationCodeViewModel

    override fun getContentView(): Int = R.layout.fragment_verification_code_frgment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = args.userName

        // Initialize CallBackInterface
        getViewModel()?.setCallBackInterface(this)

        // VerificationCode code Submit Button Listener
        mDataBinding?.submitBtn?.setOnClickListener {
            var code = mDataBinding?.verificationCode?.text.toString()

            if (code.isEmpty() || code.length < 6){
                Toast.makeText(requireContext(), "Enter Valid Code", Toast.LENGTH_SHORT).show()
            }else{
                confirmSignUpFunctionality(code)
            }
        }
    }

    // Method for confirmSignUp
    private fun confirmSignUpFunctionality(code: String) {
           getViewModel()?.confirmSignUpFunctionality(userName, code)
    }

    // CallBackInterface callBack Method
    override fun callBack(status: String, flag : String) {

        if (flag == "SignUpCompleted"){
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
            findNavController().navigate(VerificationCodeFrgmentDirections.actionVerificationCodeFrgmentToSignInFragment())
        }else{
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
        }

    }


}