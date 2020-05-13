package com.devfk.ma.screeningpractice.data.Model


import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

data class Event(
    val title:String,
    val date:String,
    val image:Int,
    val hashtag:List<String>,
    val detail:String,
    val lat:String,
    val long: String
)

open class DataEvent(
    @PrimaryKey
    var id:Int=0,
    var title:String="",
    var date:String="",
    var image:Int=0,
    var hashtag:RealmList<String> = RealmList(),
    var detail:String = "",
    var lat:String = "",
    var long: String = ""
): RealmObject()