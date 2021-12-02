package com.example.demodragger.network


import com.smf.events.ui.signup.model.GetUserDetails
import com.smf.events.ui.signup.model.UserDetails
import com.smf.events.ui.signup.model.UserDetailsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiStories {

    @POST("api/authentication/user-info")
    suspend fun addUserDetails(@Body userDetails: UserDetails): UserDetailsResponse

    @GET("api/authentication/user-info")
    suspend fun getUserDetails(@Query("loginName") loginName: String): GetUserDetails

}