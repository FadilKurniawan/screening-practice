package com.devfk.ma.screeningpractice.data

import android.app.Application
import android.app.Notification
import androidx.core.app.NotificationCompat
import com.devfk.ma.screeningpractice.R
import com.devfk.ma.screeningpractice.data.Model.DataEvent
import com.devfk.ma.screeningpractice.data.Notification.NotificationHandler
import com.onesignal.OneSignal
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.createObject

class setupApp:Application() {
    override fun onCreate() {
        super.onCreate()
        OneSignalInitialization()
        RealmInitialization()
        InsertingEventData()
    }

    private fun OneSignalInitialization() {
        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .setNotificationOpenedHandler(NotificationHandler(this))
            .setNotificationReceivedHandler(NotificationHandler(this))
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }

    private fun RealmInitialization() {
        Realm.init(this)
        val configuration = RealmConfiguration.Builder()
            .name("session.db")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(0)
            .build()
        Realm.setDefaultConfiguration(configuration)
    }

    private fun InsertingEventData() {
        val realm:Realm = Realm.getDefaultInstance()
        realm.executeTransaction{
            realm.deleteAll()
        }
        val isEventStorage = realm.where(DataEvent::class.java).sort("id").findFirst()
        if(isEventStorage == null){
            realm.executeTransaction {
                val eventTitle = resources.getStringArray(R.array.event_list_title)
                val eventDate = resources.getStringArray(R.array.event_list_date)
                val eventLat = resources.getStringArray(R.array.event_list_lattitude)
                val eventLong = resources.getStringArray(R.array.event_list_longtitude)
                val eventHashtag = resources.getStringArray(R.array.event_list_hashtag)

                for (eventCount in 0 until 4) {
                    var eventItem = realm.createObject<DataEvent>(eventCount)
                    eventItem.title = eventTitle[eventCount]
                    eventItem.date = eventDate[eventCount]
                    eventItem.detail = resources.getString(R.string.txt_detail_event_item)
                    for (hashtagCount in 0 until 3) {
                        eventItem.hashtag.add(eventHashtag[hashtagCount])
                    }
                    eventItem.lattitude = eventLat[eventCount]
                    eventItem.longtitude = eventLong[eventCount]
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