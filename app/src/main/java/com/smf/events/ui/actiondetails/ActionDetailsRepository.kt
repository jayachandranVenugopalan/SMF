package com.smf.events.ui.actiondetails

import com.smf.events.helper.ApisResponse
import com.smf.events.network.ApiStories
import com.smf.events.ui.actionandstatusdashboard.model.NewRequestList
import com.smf.events.ui.quotedetailsdialog.model.BiddingQuotDto
import retrofit2.HttpException
import javax.inject.Inject

class ActionDetailsRepository @Inject constructor(var apiStories: ApiStories)  {

    suspend fun postQuoteDetails(idToken: String, bidRequestId: Int,biddingQuote: BiddingQuotDto): ApisResponse<NewRequestList> {

        return try {
            val getResponse = apiStories.postQuoteDetails(idToken, bidRequestId,biddingQuote)
            ApisResponse.Success(getResponse)
        } catch (e: HttpException) {
            ApisResponse.Error(e)
        }
    }

    // Method For Get New Request
    suspend fun getNewRequest(
        idToken: String,
        spRegId: Int,
        serviceCategoryId: Int?,
        serviceVendorOnboardingId: Int?,
        bidStatus: String
    ): ApisResponse<NewRequestList> {

        return try {
            val getResponse = apiStories.getNewRequest(
                idToken,
                spRegId,
                serviceCategoryId,
                serviceVendorOnboardingId,
                bidStatus
            )
            ApisResponse.Success(getResponse)

        } catch (e: HttpException) {
            ApisResponse.Error(e)
        }
    }
}