package com.smf.events.ui.signup

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.google.android.material.tabs.TabLayout
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentSignupBinding
import com.smf.events.helper.ApisResponse

import com.smf.events.ui.signup.model.UserDetails
import com.smf.events.ui.signup.model.UserDetailsResponse
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.roundToInt

class SignUpFragment : BaseFragment<FragmentSignupBinding, SignUpViewModel>(),
    SignUpViewModel.CallBackInterface {

    private lateinit var userName: String
    private lateinit var password: String
    private var role: String = "EVENT_ORGANIZER"
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var mobileNumber: String
    lateinit var userDetails: UserDetails

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): SignUpViewModel? =
        ViewModelProvider(this, factory).get(SignUpViewModel::class.java)

    override fun getBindingVariable(): Int = BR.signupViewModel

    override fun getContentView(): Int = R.layout.fragment_signup

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

        // SignUp Button Listener
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
        mobileNumber = "+".plus(countryCode).plus(phoneNumber)

        val attrs = mapOf(
            AuthUserAttributeKey.email() to email,
            AuthUserAttributeKey.phoneNumber() to "+".plus(countryCode).plus(phoneNumber)
        )

        val options = AuthSignUpOptions.builder()
            .userAttribute(
                AuthUserAttributeKey.name(),
                firstName.plus(" ").plus(lastName)
            )
            .userAttributes(attrs.map { AuthUserAttribute(it.key, it.value) })
            .build()

        userName = createUserName(firstName, lastName)
        password = "Service@123"

        userDetails = UserDetails(role, firstName, lastName, email, mobileNumber, userName)
        getViewModel()?.signUp(userName, password, options)
    }

    // Method Creating Unique UserName
    private fun createUserName(firstName: String, lastName: String): String {
        var first4DigitName = ""
        var last4DigitName = ""
        var randomValue = floor(Math.random() * (999999 - 100000)) + 100000

        first4DigitName = if (firstName.length > 4) {
            firstName.substring(0, 4)
        } else {
            firstName
        }
        last4DigitName = if (lastName.length > 4) {
            lastName.substring(0, 4)
        } else {
            lastName
        }
        return last4DigitName.plus(randomValue.roundToInt()).plus(first4DigitName)
    }

    // SignUp Method Observer
    private val userInfoObserver = Observer<ApisResponse<UserDetailsResponse>> { apiResponse ->
        when (apiResponse) {
            is ApisResponse.Success -> {
                showToast("success")
                Log.d("TAG", "response: success")
            }

            is ApisResponse.Error -> {
                showToast(apiResponse.exception.localizedMessage!!)
                Log.d("TAG", "response: failure ${apiResponse.exception.localizedMessage!!}")
            }
        }
    }

    // Method for tabLayout title
    private fun setTabLayout() {
        var roles: String? = "tabSelected"

        mDataBinding!!.tablayout.addTab(
            mDataBinding!!.tablayout.newTab().setText("EVENT_ORGANIZER")
        )
        mDataBinding!!.tablayout.addTab(
            mDataBinding!!.tablayout.newTab().setText("SERVICE_PROVIDER")
        )
        mDataBinding!!.tablayout.tabGravity = TabLayout.GRAVITY_FILL
        mDataBinding!!.tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                roles = tab?.text.toString()
                Log.d("TAG", "onTabSelected: roles")
                tabSelected(roles!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    }

    fun tabSelected(roles: String) {
        Log.d("TAG", "tabSelected: $roles")
        role = roles

    }

    // CallBackInterface Override Method
    override fun callBack(status: String) {

        if (status == "signUpResult") {
            //Api post call for userDetails
            getViewModel()?.setUserDetails(userDetails)
                ?.observe(viewLifecycleOwner, userInfoObserver)
            val action =
                SignUpFragmentDirections.actionSignUpFragmentToVerificationCodeFrgment(userName)
            findNavController().navigate(action)

        } else {
            Toast.makeText(requireContext(), "Param Details Not Correct", Toast.LENGTH_SHORT).show()
        }
    }

}