package com.devfk.ma.screeningpractice.data.Model


import com.google.gson.annotations.SerializedName

data class DataGuest(
    val avatar: String,
    val email: String,
    @SerializedName("first_name")
    val firstName: String,
    val id: Int,
    @SerializedName("last_name")
    val lastName: String
)