package com.smf.events.network


import com.smf.events.helper.EvenTypes
import com.smf.events.ui.actionandstatusdashboard.model.NewRequestList
import com.smf.events.ui.dashboard.model.ActionAndStatus
import com.smf.events.ui.dashboard.model.AllServices
import com.smf.events.ui.dashboard.model.Branches
import com.smf.events.ui.dashboard.model.ServiceCount
import com.smf.events.ui.emailotp.model.GetLoginInfo
import com.smf.events.ui.quotebrief.model.QuoteBrief
import com.smf.events.ui.quotedetailsdialog.model.BiddingQuote
import com.smf.events.ui.signup.model.GetUserDetails
import com.smf.events.ui.signup.model.UserDetails
import com.smf.events.ui.signup.model.UserDetailsResponse
import retrofit2.http.*

interface ApiStories {

    @POST("epm-no-auth/api/authentication/user-info")
    suspend fun addUserDetails(@Body userDetails: UserDetails): UserDetailsResponse

    @GET("epm-no-auth/api/authentication/user-info")
    suspend fun getUserDetails(@Query("loginName") loginName: String): GetUserDetails

    @GET("epm-event/api/events/event-template-questionnaire/184")
    suspend fun get184Types(@Header("Authorization") idToken: String): EvenTypes

    @GET("epm-user/api/app-authentication/login")
    suspend fun getLoginInfo(@Header("Authorization") idToken: String): GetLoginInfo

    @GET("epm-service/api/app-services/service-counts/{sp-reg-id}")
    suspend fun getServiceCount(
        @Header("Authorization") idToken: String,
        @Path("sp-reg-id") spRegId: Int
    ): ServiceCount

    @GET("epm-service/api/app-services/services/{sp-reg-id}")
    suspend fun getAllServices(
        @Header("Authorization") idToken: String,
        @Path("sp-reg-id") spRegId: Int
    ): AllServices

    @GET("epm-service/api/app-services/service-branches/{sp-reg-id}")
    suspend fun getServicesBranches(
        @Header("Authorization") idToken: String,
        @Path("sp-reg-id") spRegId: Int,
        @Query("serviceCategoryId") serviceCategoryId: Int
    ): Branches
    @GET("epm-service/api/app-services/service-branches/{sp-reg-id}")
    suspend fun getServicesBranchesforAllservice(
        @Header("Authorization") idToken: String,
        @Path("sp-reg-id") spRegId: Int,
    ): Branches

    @GET("epm-service/api/app-services/service-provider-bidding-counts/{sp-reg-id}")
    suspend fun getActionAndStatus(
        @Header("Authorization") idToken: String,
        @Path("sp-reg-id") spRegId: Int,
        @Query("serviceCategoryId") serviceCategoryId: Int?,
        @Query("serviceVendorOnboardingId") serviceVendorOnboardingId: Int?
    ): ActionAndStatus

    @GET("epm-service/api/app-services/bidding-request-info/{sp-reg-id}")
    suspend fun getNewRequest(
        @Header("Authorization") idToken: String,
        @Path("sp-reg-id") spRegId: Int,
        @Query("serviceCategoryId") serviceCategoryId: Int?,
        @Query("serviceVendorOnboardingId") serviceVendorOnBoardingId: Int?,
        @Query("bidStatus") bidStatus: String
    ): NewRequestList

    @PUT("epm-service/api/app-services/accept-bid/{bid-request-id}")
    suspend fun postQuoteDetails(
        @Header("Authorization") idToken: String,
        @Path("bid-request-id") bidRequestId: Int,
       @Body biddingQuote: BiddingQuote
    ): NewRequestList


    @GET("epm-service/api/app-services/order-info/{bid-request-Id}")
    suspend fun getQuoteBrief(
        @Header("Authorization") idToken: String,
        @Path("bid-request-Id") bidRequestId: Int
    ): QuoteBrief








}