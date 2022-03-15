package com.smf.events.ui.dashboard

import com.smf.events.helper.ApisResponse
import com.smf.events.helper.EvenTypes
import com.smf.events.network.ApiStories
import com.smf.events.ui.dashboard.model.AllServices
import com.smf.events.ui.dashboard.model.ServiceCount
import retrofit2.HttpException
import javax.inject.Inject

class DashBoardRepository @Inject constructor(var apiStories: ApiStories) {

    suspend fun get184Types(idToken: String): ApisResponse<EvenTypes> {
        return try {

            val getResponse = apiStories.get184Types(idToken)
            ApisResponse.Success(getResponse)

        } catch (e: HttpException) {
            ApisResponse.Error(e)
        }
    }

    suspend fun getServiceCount(idToken: String, spRegId: Int): ApisResponse<ServiceCount> {

        return try {
            val getResponse = apiStories.getServiceCount(idToken, spRegId)
            ApisResponse.Success(getResponse)

        } catch (e: HttpException) {
            ApisResponse.Error(e)
        }
    }

    suspend fun getAllServices(idToken: String, spRegId: Int): ApisResponse<AllServices> {

        return try {
            val getResponse = apiStories.getAllServices(idToken, spRegId)
            ApisResponse.Success(getResponse)

        } catch (e: HttpException) {
            ApisResponse.Error(e)
        }
    }
}