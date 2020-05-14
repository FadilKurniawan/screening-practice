package com.devfk.ma.screeningpractice.ui.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.devfk.ma.screeningpractice.R
import com.devfk.ma.screeningpractice.data.Model.DataEvent
import com.devfk.ma.screeningpractice.data.Model.User
import com.devfk.ma.screeningpractice.ui.Component.CustomAlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), View.OnClickListener{

    companion object {
        const val EVENT_CODE_ACTIVITY = 0
        const val GUEST_CODE_ACTIVITY = 1
        fun getLaunchIntent(from: Context) = Intent(from, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    lateinit var realm: Realm
    //Firebase Analytics
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initialization()
    }

    private fun initialization() {
        realm = Realm.getDefaultInstance()
        //firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val user = realm.where(User::class.java).findFirst()
        if (user != null) {
            val str = user.name
            pushAnalytics("login_name","login_executed",str)
            txvName.text = str
        }else{
            signOut()
        }
        btnEvent.setOnClickListener(this)
        btnGuest.setOnClickListener(this)
        btnSignOut.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnEvent->goToEventpage()
            R.id.btnGuest->goToGuestpage()
            R.id.btnSignOut->signOut()
        }

    }

    private fun goToGuestpage() {
        val intent = Intent(this, GuestActivity::class.java)
        startActivityForResult(intent, GUEST_CODE_ACTIVITY)
    }

    private fun goToEventpage() {
        val intent = Intent(this, EventActivity::class.java)
        startActivityForResult(intent, EVENT_CODE_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == EVENT_CODE_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                val text = data!!.getStringExtra("event")
                pushAnalytics("event_name","event_selected",text)
                btnEvent.text = text
            }
        }else if(requestCode == GUEST_CODE_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                val text = data!!.getStringExtra("guestName")
                val num = data.getStringExtra("guestAge")
                pushAnalytics("guest_name","guest_selected",text)
                btnGuest.text = text
                pop(num.toInt())
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun pushAnalytics(bundle_param: String, selected:String, value: String) {
        val bundle = Bundle()
        bundle.putString(bundle_param, value)
        firebaseAnalytics?.logEvent(selected, bundle)
    }

    private fun pop(i: Int) {
        var alertDialog: CustomAlertDialog = CustomAlertDialog(this)
        alertDialog.setTitleandContent("Month is ${primaCheck(i)}",getResourceImage(i),"close")
        alertDialog.show()
    }

    private fun primaCheck(month: Int): String {
        if (month>1) {
            for(i in 2..Math.sqrt(month.toDouble()).toInt()) {
                if(month % i == 0) return "Prima"
            }
            return "Prima"
        }
        else return "not Prima"
    }

    private fun getResourceImage(i: Int): Int {
        if(i%2==0 && i%3==0){
            return R.drawable.ic_ios
        }else if(i%2==0){
            return R.drawable.ic_blackberry
        }else if(i%3==0){
            return R.drawable.ic_android
        }else{
            return R.drawable.feature_phone
        }
    }

    private fun signOut() {
        val user = FirebaseAuth.getInstance().currentUser
        deleteUser()
        if (user != null) {
            FirebaseAuth.getInstance().signOut()
        }
        startActivity(MainActivity.getLaunchIntent(this))
    }

    private fun deleteUser() {
        realm.executeTransaction{
            realm.delete(User::class.java)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

