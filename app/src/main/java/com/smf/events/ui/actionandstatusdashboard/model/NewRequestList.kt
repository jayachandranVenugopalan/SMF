package com.smf.events.ui.actionandstatusdashboard.model

data class NewRequestList(
    val data: Data,
    val result: Result,
    val success: Boolean
)

data class Data(
    val cost: Any,
    val costingType: Any,
    val serviceName: Any,
    val serviceProviderBidRequestDtos: List<ServiceProviderBidRequestDto>,
    val serviceVendorBranchDto: Any
)

data class Result(
    val info: String
)

data class ServiceAddressDto(
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val country: String,
    val knownVenue: Boolean,
    val state: String,
    val zipCode: String
)

data class ServiceProviderBidRequestDto(
    val bidAcceptedDate: Any,
    val bidCurrencyUnit: Any,
    val bidRejected: Any,
    val bidRejectedDate: Any,
    val bidRequestId: Int,
    val bidRequested: Any,
    val bidRequestedDate: String,
    val bidStatus: String,
    val bidSubmitted: Any,
    val bidTimedOut: Any,
    val biddingCutOffDate: String,
    val branchAddress: Any,
    val branchName: String,
    val cost: String,
    val costingType: String,
    val currencyType: Any,
    val eventDate: String,
    val eventId: Int,
    val eventName: String,
    val eventOrganizerId: Any,
    val eventServiceDescriptionId: Int,
    val isExistingUser: Boolean,
    val latestBidValue: Any,
    val lostBid: Any,
    val pendingForQuote: Any,
    val preferredSlots: Any,
    val rejectedBidComment: Any,
    val rejectedBidReason: Any,
    val serviceAddress: String,
    val serviceAddressDto: ServiceAddressDto,
    val serviceBranchDto: Any,
    val serviceCategoryId: Int,
    val serviceDate: String,
    val serviceDone: Any,
    val serviceName: String,
    val serviceProviderEmail: Any,
    val serviceProviderId: Any,
    val serviceProviderName: Any,
    val serviceVendorOnboardingId: Int,
    val spRegId: Int,
    val timeLeft: Double,
    val wonBid: Any
)