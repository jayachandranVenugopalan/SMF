package com.smf.events.ui.actionandstatusdashboard

import com.smf.events.helper.ApisResponse
import com.smf.events.helper.EvenTypes
import com.smf.events.network.ApiStories
import com.smf.events.ui.actionandstatusdashboard.model.NewRequestList
import com.smf.events.ui.emailotp.model.GetLoginInfo
import retrofit2.HttpException
import javax.inject.Inject

class ActionsAndStatusRepository @Inject constructor(var apiStories: ApiStories) {

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