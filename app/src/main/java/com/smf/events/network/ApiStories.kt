package com.smf.events.network


import com.smf.events.helper.EvenTypes
import com.smf.events.ui.signup.model.GetUserDetails
import com.smf.events.ui.signup.model.UserDetails
import com.smf.events.ui.signup.model.UserDetailsResponse
import retrofit2.http.*

interface ApiStories {

    @POST("epm-no-auth/api/authentication/user-info")
    suspend fun addUserDetails(@Body userDetails: UserDetails): UserDetailsResponse

    @GET("epm-no-auth/api/authentication/user-info")
    suspend fun getUserDetails(@Query("loginName") loginName: String): GetUserDetails

    @GET("epm-event/api/events/event-types")
    suspend fun getEventTypes(@Header("Authorization")idToken :String) : EvenTypes

    @GET("epm-event/api/events/event-template-questionnaire/184")
    suspend fun get184Types(@Header("Authorization")idToken :String) : EvenTypes
}