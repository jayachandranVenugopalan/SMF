package com.smf.events.ui.actionandstatusdashboard

import android.app.Application
import com.smf.events.base.BaseViewModel
import com.smf.events.databinding.FragmentActionsAndStatusBinding
import com.smf.events.ui.dashboard.model.ActionAndStatusCount
import javax.inject.Inject

class ActionsAndStatusViewModel @Inject constructor(application: Application) :
    BaseViewModel(application) {
    fun actionAndStatusCount(
        mDataBinding: FragmentActionsAndStatusBinding,
        actionAndStatusData: ActionAndStatusCount
    ) {
        mDataBinding.txPendtingitems.text="${actionAndStatusData?.actionCount.toString()} PendingItems"
        mDataBinding.txPendingstatus.text="${actionAndStatusData?.statusCount.toString()} Status"

    }


}