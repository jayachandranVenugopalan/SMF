package com.smf.events.ui.signin

import com.example.demodragger.network.ApiStories
import com.smf.events.helper.ApisResponse
import com.smf.events.ui.signup.model.GetUserDetails
import retrofit2.HttpException
import javax.inject.Inject

class SignInRepository  @Inject constructor(var apiStories: ApiStories){

    suspend fun getUserDetails(loginName: String): ApisResponse<GetUserDetails> {

        return try {
            val getResponse = apiStories.getUserDetails(loginName)
            ApisResponse.Success(getResponse)

        } catch (e: HttpException) {
            ApisResponse.Error(e)
        }
    }
}