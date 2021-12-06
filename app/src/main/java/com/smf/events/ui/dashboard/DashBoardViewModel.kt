package com.smf.events.ui.dashboard

import android.app.Application
import com.smf.events.base.BaseViewModel
import com.smf.events.ui.signup.SignUpRepository
import javax.inject.Inject

class DashBoardViewModel  @Inject constructor(
    private val dashBoardRepository: DashBoardRepository,
    application: Application
) : BaseViewModel(application) {


}