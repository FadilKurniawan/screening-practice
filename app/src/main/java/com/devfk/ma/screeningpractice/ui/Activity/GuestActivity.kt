package com.devfk.ma.screeningpractice.ui.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.devfk.ma.screeningpractice.ui.Adapter.GuestAdapter
import com.devfk.ma.screeningpractice.R
import com.devfk.ma.screeningpractice.data.Interface.IGuest
import com.devfk.ma.screeningpractice.data.Model.DataGuest
import com.devfk.ma.screeningpractice.data.Model.Response
import com.devfk.ma.screeningpractice.data.Presenter.GuestPresenter
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_guest.*
import kotlinx.android.synthetic.main.app_bar.*

class GuestActivity : AppCompatActivity(), AdapterView.OnItemClickListener , IGuest, View.OnClickListener{
    var resultName:String =""
    var resultAge:Int =0
    var pages = 1
    var per_pages = 10
    var total_pages:Int = 0
    var loadMore = false
    var listData: ArrayList<DataGuest> = ArrayList()
    lateinit var guestAdapter: GuestAdapter
    lateinit var swipe: SwipeRefreshLayout
    lateinit var realm: Realm
    lateinit var dataGuest:RealmResults<DataGuest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest)
        initialization()
        RefreshListener()
        ScrolldownListener()
    }

    private fun initialization() {
        realm = Realm.getDefaultInstance()
        headerText.text = resources.getString(R.string.txt_toolbar_title_guest)
        GuestPresenter(this).getDataGuest(pages,per_pages)
        loadview.visibility = View.VISIBLE
        loadingMore.visibility = View.GONE
        gridView.onItemClickListener = this
        btn_back.setOnClickListener(this)
        btn_media.visibility = View.GONE
        btn_search.visibility = View.GONE
        swipe = findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
    }

    private fun ScrolldownListener() {
        gridView.setOnScrollListener(object : AbsListView.OnScrollListener{
            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0 && pages+1 <= total_pages){
                    if(!loadMore){
                        loadingMore.visibility = View.VISIBLE
                        loadMore = true
                        GuestPresenter(this@GuestActivity).getDataGuest(pages+1,per_pages)
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
//                Toast.makeText(this@GuestActivity,"position: ${gridView.}",Toast.LENGTH_LONG).show()
            }

        })
    }


    private fun RefreshListener() {
        swipe.setOnRefreshListener {
            pages=1
            GuestPresenter(this).getDataGuest(pages,per_pages)
        }
    }

    override fun onBackPressed() {
        if(resultName.isNotEmpty()){
            sendDataBack()
        }
        super.onBackPressed()
    }

    private fun sendDataBack() {
        val intent = Intent().apply {
            putExtra("guestName",resultName)
            putExtra("guestAge",resultAge.toString())
        }
        setResult(Activity.RESULT_OK,intent)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var item: DataGuest = parent?.getItemAtPosition(position) as DataGuest
        resultName = item.firstName + item.lastName
        resultAge = item.id!!
        onBackPressed()
    }

    override fun onGuestList(response: Response<DataGuest>) {
        if(swipe.isRefreshing){
            swipe.isRefreshing = false
        }
        if(response.page!! >pages){
            loadingMore.visibility = View.GONE
            pages = response.page
            guestAdapter.notifyDataSetChanged()
            loadMore = false
        }else {
            dataGuest = realm.where(DataGuest::class.java).findAll()
            total_pages = response.totalPages
            guestAdapter = GuestAdapter(this,dataGuest)
            gridView.adapter =  guestAdapter
            loadview.visibility = View.GONE
        }
    }

    override fun onDataError(error: String?) {
        if(swipe.isRefreshing){
            swipe.isRefreshing = false
        }
        loadview.visibility = View.GONE
        Toast.makeText(this,"Error: $error", Toast.LENGTH_LONG).show()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_back -> onBackPressed()
        }
    }
}
