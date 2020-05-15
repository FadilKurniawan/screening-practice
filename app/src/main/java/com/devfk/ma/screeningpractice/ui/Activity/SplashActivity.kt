package com.devfk.ma.screeningpractice.ui.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.devfk.ma.screeningpractice.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    lateinit var handler: Handler
    lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(2)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        fetchRemoteVersion()


        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)


    }

    private fun fetchRemoteVersion() {
        tvVersionApp.text = resources.getString(R.string.txt_version)+" "+ remoteConfig.getString(resources.getString(R.string.txt_remote_version_app))

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful()) {
                    val updated: Boolean = it.result!!
                    Log.d("FRemoteConfig", "Config params updated: $updated")
//                    Toast.makeText(
//                        this, "Fetch and activate succeeded",
//                        Toast.LENGTH_SHORT
//                    ).show()
                } else {
                    Log.d("FRemoteConfig", "Config params updated FAIL!!!")
//                    Toast.makeText(
//                        this, "Fetch failed",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
    }
}
