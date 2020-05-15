package com.devfk.ma.screeningpractice.data.Model


import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DataEvent(
    @PrimaryKey
    var id:Int=0,
    var title:String="",
    var date:String="",
    var image:Int=0,
    var hashtag:RealmList<String> = RealmList(),
    var detail:String = "",
    var lattitude:String = "",
    var longtitude: String = ""
): RealmObject()