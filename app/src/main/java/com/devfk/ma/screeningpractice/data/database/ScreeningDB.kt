package com.devfk.ma.screeningpractice.data.database

import android.app.Application
import com.devfk.ma.screeningpractice.R
import com.devfk.ma.screeningpractice.data.Model.DataEvent
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.createObject

class ScreeningDB:Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        val configuration = RealmConfiguration.Builder()
            .name("session.db")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(0)
            .build()
        Realm.setDefaultConfiguration(configuration)


        val realm:Realm = Realm.getDefaultInstance()
//        realm.executeTransaction{
//            realm.deleteAll()
//        }

        val isEventStorage = realm.where(DataEvent::class.java).sort("id").findFirst()

        if(isEventStorage == null){
            realm.executeTransaction {

                val eventTitle = resources.getStringArray(R.array.event_list_title)
                val eventDate = resources.getStringArray(R.array.event_list_date)
                val eventLat = resources.getStringArray(R.array.event_list_lat)
                val eventLong = resources.getStringArray(R.array.event_list_long)
                val eventHashtag = resources.getStringArray(R.array.event_list_hashtag)

                for (eventCount in 0 until 4) {
                    var eventItem = realm.createObject<DataEvent>(eventCount)
                    eventItem.title = eventTitle[eventCount]
                    eventItem.date = eventDate[eventCount]
                    eventItem.detail = resources.getString(R.string.txt_detail_event_item)
                    for (hashtagCount in 0 until 3) {
                        eventItem.hashtag.add(eventHashtag[hashtagCount])
                    }
                    eventItem.lat = eventLat[eventCount]
                    eventItem.long = eventLong[eventCount]
                    eventItem.image = getDrawableFromResource(eventCount)
                }
            }
        }
    }

    private fun getDrawableFromResource(eventCount: Int): Int {
        when(eventCount){
            0 -> return R.drawable.event1
            1 -> return R.drawable.event3
            2 -> return R.drawable.event2
            3 -> return R.drawable.event4
        }
        return 0
    }

}