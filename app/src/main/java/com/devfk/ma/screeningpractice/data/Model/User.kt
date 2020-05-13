package com.devfk.ma.screeningpractice.data.Model

import io.realm.RealmObject

open class User (
    var name:String="",
    var email:String=""
):RealmObject()

