package com.smf.events.ui.dashboard.model


data class ActionAndStatus(
    val success: Boolean,
    val result: Results
)


data class Results(val info: String)