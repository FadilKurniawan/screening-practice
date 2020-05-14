package com.devfk.ma.screeningpractice.ui.Fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.devfk.ma.screeningpractice.R
import com.devfk.ma.screeningpractice.data.Model.DataEvent
import com.devfk.ma.screeningpractice.ui.Component.CarouselAdapter
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import io.realm.Realm
import io.realm.RealmResults


class MapFragment : Fragment(), OnMapReadyCallback, CarouselAdapter.EventListener{

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment().apply {
        }
    }

    private val SOURCE_ID = "SOURCE_ID"
    private val ICON_ID = "ICON_ID"
    private val LAYER_ID = "LAYER_ID"

    var viewPager: ViewPager? = null
    lateinit var mapView: MapView
    lateinit var mapBox: MapboxMap
    lateinit var realm: Realm
    lateinit var eventItem:RealmResults<DataEvent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(context!!,resources.getString(R.string.mapBox_accessToken))
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_map, container, false)
        viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        initialization()

        return view
    }

    private fun initialization() {
        eventItem = realm.where(DataEvent::class.java).sort("id").findAll()
        if(eventItem!=null){
            var adapter = CarouselAdapter(eventItem, activity)
            viewPager?.adapter = adapter
            mapView.getMapAsync(this)
        }

        viewPager?.clipToPadding = false
        viewPager?.setPadding(100,0,100,0)

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapBox = mapboxMap
        val symbolLayerIconFeatureList: MutableList<Feature> = ArrayList()

        for (index in 0 until eventItem.size){
            symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(eventItem[index]!!.longtitude.toDouble(),eventItem[index]!!.lattitude.toDouble())))
        }

        mapBox.setStyle(Style.MAPBOX_STREETS, object :Style.OnStyleLoaded {
            override fun onStyleLoaded(style: Style) {
                changeMarker(0)
                animateMapCamera(mapBox,eventItem[0]!!.longtitude.toDouble(),eventItem[0]!!.lattitude.toDouble())
                setScrolled()
            }

        })
    }

    private fun setScrolled() {
        viewPager?.setOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                changeMarker(position)
                animateMapCamera(mapBox,eventItem[position]!!.longtitude.toDouble(),eventItem[position]!!.lattitude.toDouble())
            }

            override fun onPageSelected(position: Int) {
            }

        })
    }

    private fun changeMarker(position: Int) {
        mapBox.clear()
        for(index in 0 until eventItem.size){
            if(index!=position){
                val iconFactory = IconFactory.getInstance(context!!)
                val bm = BitmapFactory.decodeResource(resources, R.drawable.ic_marker_unselected)
                val icon: com.mapbox.mapboxsdk.annotations.Icon? = iconFactory.fromBitmap(
                    Bitmap.createScaledBitmap(bm, 60, 70, false))
                mapBox.addMarker(
                    MarkerOptions()
                        .position(LatLng(eventItem[index]!!.lattitude.toDouble(), eventItem[index]!!.longtitude.toDouble()))
                        .icon(icon)
                )
            }else{
                val iconFactory = IconFactory.getInstance(context!!)
                val bm = BitmapFactory.decodeResource(resources, R.drawable.ic_marker_selected)
                val icon: com.mapbox.mapboxsdk.annotations.Icon? = iconFactory.fromBitmap(
                    Bitmap.createScaledBitmap(bm, 60, 70, false))
                mapBox.addMarker(
                    MarkerOptions()
                        .position(LatLng(eventItem[index]!!.lattitude.toDouble(), eventItem[index]!!.longtitude.toDouble()))
                        .icon(icon)
                )
            }
        }

    }

    private fun animateMapCamera(mapboxMap: MapboxMap, longtitude: Double, lattitude: Double) {
        val position = CameraPosition.Builder()
            .target(LatLng(lattitude, longtitude)) // Sets the new camera position
            .zoom(13.0) // Sets the zoom
            .bearing(10.0) // Rotate the camera
            .tilt(30.0) // Set the camera tilt
            .build() // Creates a CameraPosition from the builder


        mapboxMap.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(position), 3000
        )
    }

    override fun onEvent(data: Int) {

    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        realm.close()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
