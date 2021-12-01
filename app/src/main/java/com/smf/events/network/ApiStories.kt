package com.example.demodragger.network


import com.smf.events.ui.signup.model.UserDetails
import com.smf.events.ui.signup.model.UserDetailsResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiStories {

    @POST("api/authentication/user-info")
    suspend fun addUserDetails(@Body userDetails: UserDetails) : UserDetailsResponse
}