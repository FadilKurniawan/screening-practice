package com.devfk.ma.screeningpractice.data.Presenter

import android.content.Context
import com.devfk.ma.screeningpractice.data.Interface.IGuest
import com.devfk.ma.screeningpractice.data.Model.DataGuest
import com.devfk.ma.screeningpractice.data.service.APIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm


class GuestPresenter (context:Context){
    val GuestView = context as IGuest

    fun getDataGuest(page: Int, per_page: Int){
//================ Retrofit 2 + rxJava
        val subscribe = APIService.create().getGuest(page, per_page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->
                println("*** response: $it")
                Realm.getDefaultInstance().executeTransaction {realm ->
                    realm.insertOrUpdate(it.data)
                }
                GuestView.onGuestList(it)
            }, {
                GuestView.onDataError(it.message)
            })
    }
}