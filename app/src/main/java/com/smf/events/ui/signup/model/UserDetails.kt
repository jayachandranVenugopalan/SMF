package com.smf.events.ui.signup.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetails(
    var role:String,
    var firstName:String,
    var lastName:String,
    var  email:String,
    var  mobileNumber: String,
    var userName:String
) : Parcelable

data class Reponsetoken(val token:String)


