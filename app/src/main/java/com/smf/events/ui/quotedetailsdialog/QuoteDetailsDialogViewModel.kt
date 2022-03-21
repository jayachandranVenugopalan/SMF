package com.smf.events.ui.quotedetailsdialog

import android.app.Application
import androidx.lifecycle.liveData
import com.smf.events.base.BaseDialogViewModel
import com.smf.events.databinding.FragmentQuoteDetailsDialogBinding
import com.smf.events.ui.quotedetailsdialog.model.BiddingQuote
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class QuoteDetailsDialogViewModel @Inject constructor(private val quoteDetailsRepository: QuoteDetailsRepository, application: Application):BaseDialogViewModel(application) {
    fun OnCLickok(
        mDataBinding: FragmentQuoteDetailsDialogBinding?,
        bidRequestId: Int,
        costingType: String
    ) {
        mDataBinding?.btnOk?.setOnClickListener {
            var firstCheckbox=mDataBinding?.quoteCheckBox1

            if (firstCheckbox!!.isChecked){

                callBackInterface?.callBack("Checked")
            }

        }
    }
    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBack Interface
    interface CallBackInterface {
         fun callBack(status: String)
    }
    fun postQuoteDetails(idToken: String, bidRequestId: Int,biddingQuote: BiddingQuote) = liveData(Dispatchers.IO) {
        emit(quoteDetailsRepository.postQuoteDetails(idToken, bidRequestId,biddingQuote))
    }
}