package com.devfk.ma.screeningpractice.ui.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.devfk.ma.screeningpractice.R
import com.devfk.ma.screeningpractice.data.Model.DataEvent
import com.devfk.ma.screeningpractice.data.Model.Event
import com.devfk.ma.screeningpractice.ui.Adapter.EventAdapter
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.app_bar.*


class EventActivity : AppCompatActivity() , AdapterView.OnItemClickListener, View.OnClickListener{

    var result:String =""
    var eventList:ArrayList<Event> = ArrayList<Event>()
    lateinit var realm:Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        initialization()
    }

    private fun initialization() {
        headerText.text = resources.getString(R.string.txt_toolbar_title_event)
        fragment_container.visibility = View.GONE

        realm = Realm.getDefaultInstance()
        var itemEvent = realm.where(DataEvent::class.java).sort("id").findAll()

        lv_event.adapter = EventAdapter(itemEvent)
        lv_event.onItemClickListener = this

        btn_back.setOnClickListener(this)
        btn_media.setOnClickListener(this)
        btn_search.setOnClickListener(this)
        btn_media.visibility = View.VISIBLE
        btn_search.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if(result.isNotEmpty()){
            sendDataBack()
        }
        if(supportFragmentManager.backStackEntryCount>0){
            btn_media.setBackgroundResource(R.drawable.ic_map_view)
        }
        super.onBackPressed()
    }

    private fun sendDataBack() {
        val intent = Intent().apply {
            putExtra("event",result)
        }
        setResult(Activity.RESULT_OK,intent)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var item:DataEvent = parent?.getItemAtPosition(position) as DataEvent
        result = item.title
        onBackPressed()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_back ->{
                onBackPressed()
            }
            R.id.btn_media ->{
                if(fragment_container.visibility== View.GONE||supportFragmentManager.backStackEntryCount==0) {
//                    onMediaClick()
                    btn_media.setBackgroundResource(R.drawable.ic_list_view)
                }else{
                    onBackPressed()
                    btn_media.setBackgroundResource(R.drawable.ic_map_view)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun onMediaClick() {
//            fragment_container.visibility = View.VISIBLE
//            val mapFragment = MapFragment.newInstance(eventList)
//            val manager = supportFragmentManager
//            val transaction = manager.beginTransaction()
//            transaction.replace(R.id.fragment_container,mapFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
    }



}

