package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

import org.json.JSONObject

import java.sql.Timestamp
import java.util.*

import com.google.maps.android.PolyUtil
import java.text.SimpleDateFormat

/* *************************************************************** */

class GoogleAPI(activity:MainActivity){

    /* Google Places ********************************************* */
    lateinit var placesClient : PlacesClient
    var mCommute : Commute = Commute()
    var responseReceived = 0
    var responseReceivedMAX = 5
    //var GoogleKey = R.string.GoogleMapsKey
    var mFragmentEdit : FragmentEdit? = null
    val mActivity= activity
    var mContext : Context? = null
    var mService : NotificationService? = null
    var mSender : String? = null

    val errorLimitUp = 60
    val errorLimitDown = -300

    fun setActivityContext(activity : FragmentEdit, context: Context){
        mFragmentEdit = activity
        mContext = context
        setupPlacesAutocomplete()
    }

    fun setServiceContext(activity : NotificationService){
        mService = activity
    }

    init{}

    private fun setupPlacesAutocomplete(){
        var placeFields = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS
        )

        Places.initialize(mContext!!, MainActivity.GoogleKey!!)
        placesClient = Places.createClient(mContext!!)

        val autocompleteFragment_start = mFragmentEdit!!
            .childFragmentManager
            .findFragmentById(R.id.fragment_start)
                as AutocompleteSupportFragment

        autocompleteFragment_start
            .setPlaceFields(placeFields)
        autocompleteFragment_start
            .setHint("Set the start of your commute")
            .setOnPlaceSelectedListener(
                object : PlaceSelectionListener{
                    override fun onPlaceSelected(p0 : Place){
                        FragmentEdit.start = p0.address!!
                        // getPhotoAndDetail(p0.id!!.true)
                    }
                    override fun onError(p0 : Status){}
                }
            )

        val autocompleteFragment_arrival = mFragmentEdit!!
            .childFragmentManager
            .findFragmentById(R.id.fragment_arrival)
                as AutocompleteSupportFragment
        autocompleteFragment_arrival
            .setPlaceFields(placeFields)
        autocompleteFragment_arrival
            .setHint("Set the arrival of your commute")
        autocompleteFragment_arrival
            .setOnPlaceSelectedListener(
                object : PlaceSelectionListener{
                    override fun onPlaceSelected(p0 : Place){
                        FragmentEdit.arrival = p0.address!!
                        // getPhotoAndDetail(p0.id!!.true)
                    }
                    override fun onError(p0 : Status){}
                }
            )
    }

    /* Google Routes ********************************************* */
    fun requestRoute(
        sender : String,
        name : String,
        start : String,
        arrival : String,
        arrival_time : String){

        mSender = sender
        mCommute.name = name
        mCommute.start = start
        mCommute.arrival = arrival
        mCommute.arrival_time = arrival_time

        if(arrival_time != "Now"){
            mCommute.arrival_time_UTC =
                (Timestamp.valueOf(arrival_time).time/1000)
        }

        responseReceived = 0
        sendHTTP(mCommute)
    }

    fun sendHTTP(mCommute : Commute){
        var start_time : String = "Now"
        if(mCommute.arrival_time != "Now"){
            start_time = mCommute.arrival_time_UTC.toString()
        }

        var mRoute =
            "https://maps.googleapis.com/maps/" +
                    "api/directions/json?origin=" + mCommute.start +
                    "&destination=" + mCommute.arrival +
                    "&departure_time=" + start_time +
                    "&traffic_model=best_guess" +
                    "&key=" + MainActivity.GoogleKey

        val httpRequest = HTTPRequest()
        httpRequest.setActivityContext(this)
        httpRequest.execute(mRoute)
    }

    fun getResults(json : String){
        mCommute = readJSON(json)
        if(mCommute.arrival_time != "Now"){
            when(responseReceived){
                0 -> {
                    responseReceived = 1
                    responseReceivedMAX = 5
                    mCommute.start_time_UTC =
                        (mCommute.arrival_time_UTC!! -
                                mCommute.duration_val!!)
                    sendHTTP(mCommute)
                }
                1 -> {
                    responseReceivedMAX = responseReceivedMAX - 1

                    mCommute.errorTraffic =
                        (mCommute.start_time_UTC!! +
                                mCommute.duration_traffic_val!! -
                                mCommute.arrival_time_UTC!!)

                    if(((mCommute.errorTraffic!! > errorLimitUp) or
                                (mCommute.errorTraffic!! < errorLimitDown))
                        and (responseReceived > 0)){
                        mCommute.start_time_UTC =
                            (mCommute.start_time_UTC!! -
                                    mCommute.errorTraffic!!)
                        sendHTTP(mCommute)
                    } else{
                        val jdf =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        jdf.setTimeZone(TimeZone.getTimeZone("GMT+1"))
                        mCommute.start_time =
                            jdf.format(mCommute.start_time_UTC!! * 1000)

                        if(mSender == "Service"){
                            mService!!.routeRequestedReady(mCommute)
                        }
                        else if(mSender == "Activity"){
                            mActivity.routeRequestedReady(mCommute)
                        }
                        responseReceived = 0
                    }
                }
            }
        }
        else mService!!.routeRequestedReady(mCommute)
    }

    fun readJSON(json : String) : Commute{
        val jsonResponse = JSONObject(json)
        val path : MutableList<List<LatLng>> = ArrayList()

        val routes = jsonResponse
            .getJSONArray("routes")
        val legs = routes
            .getJSONObject(0)
            .getJSONArray("legs")
        val steps = legs
            .getJSONObject(0)
            .getJSONArray("steps")

        val startLocationLat = legs
            .getJSONObject(0)
            .getJSONObject("start_location")
            .getString("lat")
        val startLocationLng = legs
            .getJSONObject(0)
            .getJSONObject("start_location")
            .getString("lng")

        val arrivalLocationLat = legs
            .getJSONObject(0)
            .getJSONObject("end_location")
            .getString("lat")
        val arrivalLocationLng = legs
            .getJSONObject(0)
            .getJSONObject("end_location")
            .getString("lng")


        mCommute.start_address = legs
            .getJSONObject(0)
            .getString("start_address")
        mCommute.start_address_LatLng =
            LatLng(
                startLocationLat.toDouble(),
                startLocationLng.toDouble())

        mCommute.arrival_address = legs
            .getJSONObject(0)
            .getString("end_address")
        mCommute.arrival_address_LatLng =
            LatLng(
                arrivalLocationLat.toDouble(),
                arrivalLocationLng.toDouble())

        mCommute.distance = legs
            .getJSONObject(0)
            .getJSONObject("distance")
            .getString("text")
        mCommute.duration = legs
            .getJSONObject(0)
            .getJSONObject("duration")
            .getString("text")
        mCommute.duration_val = legs
            .getJSONObject(0)
            .getJSONObject("duration")
            .getString("value").toLong()

        if(legs.getJSONObject(0).has("duration_in_traffic")){
            mCommute.duration_traffic = legs
                .getJSONObject(0)
                .getJSONObject("duration_in_traffic")
                .getString("text")
            mCommute.duration_traffic_val = legs
                .getJSONObject(0)
                .getJSONObject("duration_in_traffic")
                .getString("value").toLong()
        }

        for(i in 0 until steps.length()){
            val points = steps
                .getJSONObject(i)
                .getJSONObject("polyline")
                .getString("points")
            path.add(PolyUtil.decode(points))
        }

        mCommute.path = path

        mCommute.raw_data = json
            .substringBefore("steps")
            .substringAfter("legs")

        return mCommute
    }

}