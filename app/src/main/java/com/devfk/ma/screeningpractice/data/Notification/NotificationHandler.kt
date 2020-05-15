package com.devfk.ma.screeningpractice.data.Notification


import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.devfk.ma.screeningpractice.R
import com.devfk.ma.screeningpractice.ui.Activity.HomeActivity
import com.onesignal.OSNotification
import com.onesignal.OSNotificationAction
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal


class NotificationHandler(application: Application): OneSignal.NotificationReceivedHandler ,OneSignal.NotificationOpenedHandler{
    var application: Application = application

    override fun notificationReceived(notification: OSNotification?) {

        val data = notification!!.payload.additionalData
        val customKey: String?

        if (data != null) {
            customKey = data.optString("customkey", null)
            if (customKey != null) Log.i(
                "OneSignalExample",
                "customkey set with value: $customKey"
            )
        }
    }

    override fun notificationOpened(result: OSNotificationOpenResult?) {
        val actionType = result!!.action.type
        val data = result!!.notification.payload.additionalData
        val customKey: String?
        Log.i(
            "OSNotificationPayload",
            "result.notification.payload.toJSONObject().toString(): " + result!!.notification.payload.toJSONObject()
                .toString()
        )

        if (data != null) {
            customKey = data.optString("customkey", null)
            if (customKey != null) Log.i(
                "OneSignalExample",
                "customkey set with value: $customKey"
            )
        }
        if (actionType == OSNotificationAction.ActionType.ActionTaken) Log.i(
            "OneSignalExample",
            "Button pressed with id: " + result!!.action.actionID
        )

        // Launch new activity using Application object
//        startApp()
    }

    private fun startApp() {
        val intent: Intent = Intent(application, HomeActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(intent)
    }
}