package com.smf.events.ui.actionandstatusdashboard

import android.app.Application
import androidx.lifecycle.liveData
import com.smf.events.base.BaseViewModel
import com.smf.events.databinding.FragmentActionsAndStatusBinding
import com.smf.events.ui.dashboard.model.ActionAndStatusCount
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ActionsAndStatusViewModel @Inject constructor(
    private val actionsAndStatusRepository: ActionsAndStatusRepository,
    application: Application
) :
    BaseViewModel(application) {

    // Method For Getting Action And Status
    fun getActionAndStatus(
        idToken: String,
        spRegId: Int,
        serviceCategoryId: Int?,
        serviceVendorOnboardingId: Int?,
    ) = liveData(Dispatchers.IO) {
        emit(
            actionsAndStatusRepository.getActionAndStatus(
                idToken,
                spRegId,
                serviceCategoryId,
                serviceVendorOnboardingId
            )
        )
    }

    // Method For Get New Request
    fun getNewRequest(
        idToken: String,
        spRegId: Int,
        serviceCategoryId: Int?,
        serviceVendorOnboardingId: Int?,
        bidStatus: String
    ) =
        liveData(Dispatchers.IO) {
            emit(
                actionsAndStatusRepository.getNewRequest(
                    idToken,
                    spRegId,
                    serviceCategoryId,
                    serviceVendorOnboardingId,
                    bidStatus
                )
            )
        }
}