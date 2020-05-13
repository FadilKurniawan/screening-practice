package com.devfk.ma.screeningpractice.data.Model


import com.devfk.ma.screeningpractice.data.Model.Ad
import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("ad")
    val ad: Ad,
    @SerializedName("data")
    val `data`: List<T>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)