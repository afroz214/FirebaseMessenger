package com.smartherd.firebasemessenger

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(var uids:String, var username:String, val profileImageUrl:String) : Parcelable {

    constructor() : this("", "", "")
}