package com.smf.events.ui.emailotp

import android.util.Log
import com.smf.events.SMFApp
import com.smf.events.helper.ApisResponse
import com.smf.events.helper.EvenTypes
import com.smf.events.helper.Tokens
import com.smf.events.network.ApiStories
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject

class EmailOTPRepository @Inject constructor(var apiStories: ApiStories)  {


    suspend fun getEventTypes(idToken: String): ApisResponse<EvenTypes> {

        return try {
            val getResponse = apiStories.getEventTypes(idToken)
            ApisResponse.Success(getResponse)

        } catch (e: HttpException) {
            ApisResponse.Error(e)
        }
    }


}