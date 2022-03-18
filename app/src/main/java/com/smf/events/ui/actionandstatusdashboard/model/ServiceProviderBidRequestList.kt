package com.smf.events.ui.actionandstatusdashboard.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceProviderBidRequestList(
    val bidRequestId: Int,
//    val bidRequestedDate: String,
//    val bidStatus: String,
//    val biddingCutOffDate: String,
    val branchName: String,
//    val cost: String,
//    val costingType: String,
//    val eventDate: String,
//    val eventId: Int,
    val eventName: String,
//    val eventServiceDescriptionId: Int,
//    val isExistingUser: Boolean,
//    val serviceAddress: String,
//    val serviceAddressDto: ServiceAddressDto,
//    val serviceCategoryId: Int,
//    val serviceDate: String,
//    val serviceName: String,
//    val serviceVendorOnboardingId: Int,
//    val spRegId: Int,
//    val timeLeft: Double,
): Parcelable
