package com.smf.events.ui.dashboard.model


data class Branches(
    val success: Boolean,
    val result: Result
)

data class Result(val info: String)