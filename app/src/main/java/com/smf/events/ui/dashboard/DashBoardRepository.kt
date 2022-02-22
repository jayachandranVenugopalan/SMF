package com.smf.events.ui.dashboard

import com.smf.events.helper.ApisResponse
import com.smf.events.helper.EvenTypes
import com.smf.events.network.ApiStories
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
}