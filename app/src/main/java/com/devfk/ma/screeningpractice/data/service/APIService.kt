package com.devfk.ma.screeningpractice.data.service

import com.devfk.ma.screeningpractice.data.Model.DataGuest
import com.devfk.ma.screeningpractice.data.Model.Response
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService{
    //getGuest
    @GET("users")
    fun getGuest(
        @Query("page") page:Int,
        @Query("per_page") per_page:Int
    ): Observable<Response<DataGuest>>

    companion object Factory {

        const val BASE_URL ="https://reqres.in/api/"

        fun create(): APIService{
            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }

    }

}