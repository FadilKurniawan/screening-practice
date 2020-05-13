package com.devfk.ma.screeningpractice.data.Interface

import com.devfk.ma.screeningpractice.data.Model.DataGuest
import com.devfk.ma.screeningpractice.data.Model.Response

interface IGuest {
    fun onGuestList(guest: Response<DataGuest>)
    fun onDataError(error: String?)
}