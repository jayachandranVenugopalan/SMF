package com.smf.events.ui.bidrejectiondialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.SMFApp
import com.smf.events.base.BaseDialogFragment
import com.smf.events.databinding.FragmentBidRejectionDialogBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.helper.Tokens
import com.smf.events.ui.bidrejectiondialog.model.ServiceProviderBidRequestDto
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import javax.inject.Inject


class BidRejectionDialogFragment(
    var bidRequestId: Int?,
    var serviceName: String,
    var code: String,
) : BaseDialogFragment<FragmentBidRejectionDialogBinding, BidRejectionDialogViewModel>(),
    BidRejectionDialogViewModel.CallBackInterface, Tokens.IdTokenCallBackInterface {

    @Inject
    lateinit var tokens: Tokens

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    lateinit var reason: String
    lateinit var idToken: String
    lateinit var serviceProviderBidRequestDto: ServiceProviderBidRequestDto

    companion object {
        const val TAG = "CustomDialogFragment"

        //take the title and subtitle form the Activity
        fun newInstance(
            bidRequestId: Int?,
            serviceName: String,
            code: String,
        ): BidRejectionDialogFragment {

            return BidRejectionDialogFragment(bidRequestId, serviceName, code)
        }

    }

    override fun getViewModel(): BidRejectionDialogViewModel =
        ViewModelProvider(this, factory).get(BidRejectionDialogViewModel::class.java)

    override fun getBindingVariable(): Int = BR.bidRejectionDialogViewModel

    override fun getContentView(): Int = R.layout.fragment_bid_rejection_dialog

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize shared preference token
        setIdToken()
    }

    override fun onStart() {
        super.onStart()
        //Setting Size for the dialog
        dialogFragmentSize()
        //Token Class CallBack Initialization
        tokens.setCallBackInterface(this)
        //BidRejectionDialog ViewModel CallBackInterface
        getViewModel().setCallBackInterface(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Reason for rejection
        getViewModel().reasonForReject(mDataBinding)
        //On OK Button CLick
        okBtnClick()
        //On cancel button click
        cancelBtnClick()
        //Setting Service ID and Service Name
        mDataBinding!!.quoteTitle.text = "You Rejected a $serviceName #$code"
    }

    // method For OkButton
    private fun okBtnClick() {
        mDataBinding?.btnOk?.setOnClickListener {
            if (reason == "Other") {
                if (mDataBinding?.etComments?.text.isNullOrEmpty()) {
                    mDataBinding?.alertMsg?.visibility = View.VISIBLE
                } else {
                    apiTokenValidationQuoteDetailsDialog("BidReject")
                }
            } else if (reason == "Reason For Rejection") {
                showToast("Please Select the reason for rejection")
            } else {
                apiTokenValidationQuoteDetailsDialog("BidReject")
            }
        }
    }

    // method For CancelButton
    private fun cancelBtnClick() {
        mDataBinding?.btnCancel?.setOnClickListener {
            dismiss()
        }
    }

    // Method For Bid Rejection Api Call
    private fun bidRejectionApiCall(idToken: String) {
        serviceProviderBidRequestDto = ServiceProviderBidRequestDto(
            bidRequestId!!,
            mDataBinding?.etComments?.text.toString(), reason
        )
        getViewModel().putBidRejection(idToken, serviceProviderBidRequestDto)
            .observe(viewLifecycleOwner, Observer { apiResponse ->
                when (apiResponse) {
                    is ApisResponse.Success -> {
                        actionDetailsFragmentListUpdate()
                        dismiss()
                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })

    }

    // Method For Send Data To actionDetails Fragment
    private fun actionDetailsFragmentListUpdate() {
        // Result to Send ActionDetails Fragment
        parentFragmentManager.setFragmentResult(
            "1", // Same request key ActionDetailsFragment used to register its listener
            bundleOf("key" to "value") // The data to be passed to ActionDetailsFragment
        )
    }

    // Call Back From BidRejectionDialogViewModel
    override fun callBack(status: String) {
        reason = status
        if (reason != "Other") {
            mDataBinding?.alertMsg?.visibility = View.INVISIBLE
        }

    }

    // Method For Set setIdToken From Shared Preferences
    private fun setIdToken() {
        var getSharedPreferences = requireActivity().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )
        idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"

    }

    private fun apiTokenValidationQuoteDetailsDialog(status: String) {
        tokens.checkTokenExpiry(
            requireActivity().applicationContext as SMFApp,
            status, idToken
        )
    }

    override suspend fun tokenCallBack(idToken: String, caller: String) {
        withContext(Main) {
            bidRejectionApiCall(idToken)
        }
    }

    //Setting Dialog Size
    private fun dialogFragmentSize() {
        var window: Window? = dialog?.window
        var params: WindowManager.LayoutParams = window!!.attributes
        params.width = ((resources.displayMetrics.widthPixels * 0.9).toInt())

        window.attributes = params
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

    }
}