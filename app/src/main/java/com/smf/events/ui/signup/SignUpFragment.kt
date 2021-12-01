package com.smf.events.ui.signup

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.google.android.material.tabs.TabLayout
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.SignUpFragmentBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.ui.signup.model.Reponsetoken
import com.smf.events.ui.signup.model.UserDetails
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.roundToInt

class SignUpFragment : BaseFragment<SignUpFragmentBinding, SignUpViewModel>(), SignUpViewModel.CallBackInterface {

    private lateinit var userName: String
    private lateinit var password: String
    private lateinit var role:String
    private lateinit var firstName:String
    private lateinit var lastName:String
    private lateinit  var  email:String
    private lateinit  var  mobileNumber: String

lateinit var userDetails: UserDetails
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): SignUpViewModel? =
        ViewModelProvider(this,factory).get(SignUpViewModel::class.java)

    override fun getBindingVariable(): Int = BR.signupViewModel

    override fun getContentView(): Int = R.layout.sign_up_fragment

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

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

        firstName = mDataBinding?.editTextFirstName?.text.toString().trim()
        lastName = mDataBinding?.editTextLastName?.text.toString().trim()
        email = mDataBinding?.editTextEmail?.text.toString().trim()
        var countryCode = mDataBinding?.cpp?.selectedCountryCode
        var phoneNumber = mDataBinding?.editTextNumber?.text?.trim()
        mobileNumber= "+".plus(countryCode).plus(phoneNumber)
        val options = AuthSignUpOptions.builder()
            .userAttribute(
                AuthUserAttributeKey.name(),
                firstName.plus(" ").plus(lastName)
            )
            .userAttribute(
                AuthUserAttributeKey.phoneNumber(),
                "+".plus(countryCode).plus(phoneNumber)
            )
            .userAttribute(AuthUserAttributeKey.email(), email)
            .build()

        userName=  createUserName(firstName,lastName)
        password = "Service@123"
        role="EventOrganiser"

       userDetails=UserDetails(role,firstName,lastName,email,mobileNumber,userName)
        getViewModel()?.signUp(userName, password, options)
    }

    // Method Creating Unique UserName
    private fun createUserName(firstName: String, lastName: String) : String{
        var first4DigitName = ""
        var last4DigitName = ""
        var randomValue = floor(Math.random() * (999999 - 100000)) + 100000

        first4DigitName = if (firstName.length > 4) {
            firstName.substring(0,4)
        }else{
            firstName
        }
        last4DigitName = if (lastName.length > 4){
            lastName.substring(0,4)
        }else{
            lastName
        }
        return last4DigitName.plus(randomValue.roundToInt()).plus(first4DigitName)
    }
    private val serviceTypeObserver = Observer<ApisResponse<Reponsetoken>> { apiResponse ->
        when (apiResponse) {
            is ApisResponse.Success -> {
                showToast("success")
                Log.d("TAG", "response: success")
            }

            is ApisResponse.Error -> {
                showToast(apiResponse.exception.localizedMessage!!)
                Log.d("TAG", "response: failure")
            }
            ApisResponse.LOADING -> {

            }
            ApisResponse.COMPLETED -> {

            }
        }
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
            getViewModel()?.setUserDetails(userDetails)?.observe(viewLifecycleOwner,serviceTypeObserver)
            val action = SignUpFragmentDirections.actionSignUpFragmentToVerificationCodeFrgment(userName)
            findNavController().navigate(action)

        }else{
            getViewModel()?.setUserDetails(userDetails)?.observe(viewLifecycleOwner,serviceTypeObserver)
            Toast.makeText(requireContext(), "Param Details Not Correct", Toast.LENGTH_SHORT).show()

        }
    }

}