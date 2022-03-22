package com.smf.events.ui.actiondetails

import android.app.Application
import androidx.lifecycle.liveData
import com.smf.events.base.BaseViewModel
import com.smf.events.ui.quotedetailsdialog.model.BiddingQuotDto
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ActionDetailsViewModel @Inject constructor(val actionDetailsRepository: ActionDetailsRepository,application: Application): BaseViewModel(application) {
    fun postQuoteDetails(idToken: String, bidRequestId: Int,biddingQuote: BiddingQuotDto) = liveData(
        Dispatchers.IO) {
        emit(actionDetailsRepository.postQuoteDetails(idToken, bidRequestId,biddingQuote))
    }
}