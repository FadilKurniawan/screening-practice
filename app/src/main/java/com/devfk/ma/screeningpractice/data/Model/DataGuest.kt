package com.devfk.ma.screeningpractice.data.Model


import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class DataGuest(
    var avatar: String = "",
    var email: String = "",
    @SerializedName("first_name")
    var firstName: String = "",
    var id: Int = 0,
    @SerializedName("last_name")
    var lastName: String  = ""
): RealmObject()