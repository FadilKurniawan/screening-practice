package com.devfk.ma.screeningpractice.data.database

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class Screening:Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val configuration = RealmConfiguration.Builder()
            .name("session.db")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(0)
            .build()
        Realm.setDefaultConfiguration(configuration)
    }
}