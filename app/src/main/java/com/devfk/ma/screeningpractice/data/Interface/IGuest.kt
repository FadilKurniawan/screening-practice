package com.devfk.ma.screeningpractice.data.Interface

import com.devfk.ma.screeningpractice.data.Model.Data
import com.devfk.ma.screeningpractice.data.Model.Response

interface IGuest {
    fun onGuestList(guest: Response<Data>)
    fun onDataError(error: String?)
}