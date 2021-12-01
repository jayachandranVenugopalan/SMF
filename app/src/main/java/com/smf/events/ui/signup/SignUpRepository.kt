package com.smf.events.ui.signup

import com.example.demodragger.network.ApiStories
import com.smf.events.helper.ApisResponse
import com.smf.events.ui.signup.model.Reponsetoken
import com.smf.events.ui.signup.model.UserDetails
import retrofit2.HttpException
import javax.inject.Inject

class SignUpRepository @Inject constructor(var apiStories: ApiStories){

    suspend fun setUserDetails(userDetails: UserDetails): ApisResponse<Reponsetoken> {

        return try {
            val callApi = apiStories.addUserDetails(userDetails)
            ApisResponse.Success(callApi)
        } catch (e: HttpException) {
            ApisResponse.Error(e)
//            val errorMessage = errorMessagefromapi(e)
//            ApisResponse.CustomError(errorMessage!!)

        }
    }
}