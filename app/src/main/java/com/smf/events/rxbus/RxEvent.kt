package com.smf.events.rxbus

import com.smf.events.ui.dashboard.model.ActionAndStatusCount
import com.smf.events.ui.dashboard.model.ServiceAndCategoryId

class RxEvent {

    data class ActionAndStatus(var actionAndStatusCount : ActionAndStatusCount, val serviceAndCategoryId: ServiceAndCategoryId)

}