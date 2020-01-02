package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import android.util.Log
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

class GoogleAPI(){

    /* Google Places ********************************************* */
    lateinit var placesClient : PlacesClient

    var responseReceived = 0
    var responseReceivedMAX = 5
    //var GoogleKey = R.string.GoogleMapsKey
    var mFragmentEdit : FragmentEdit? = null
    var mActivity: MainActivity?= null //activity
    var mContext : Context? = null
    var mService : NotificationService? = null
    var mSender : String? = null
    var mPosition : Int = -1
    var isNew : Boolean? = null
    var routeName : String = ""
    var routeStart : String = ""
    var routeArrival : String = ""
    var routeArrivalTime : String = ""
    var routeArrivalTimeUTC : Long? = null
    var routeStartTimeUTC : Long? = null
    val errorLimitUp = 60
    val errorLimitDown = -300

    lateinit var dateTime : List<String>
    lateinit var date : List<String>
    lateinit var time : List<String>


    fun setActivityContext(activity : FragmentEdit, context: Context){
        mFragmentEdit = activity
        mContext = context
        setupPlacesAutocomplete()
    }
    fun setActivity(activity : MainActivity){
        mActivity = activity
    }

    fun setService(activity : NotificationService){
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
            //.setHint("Set the start point")
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
            //.setHint("Set the arrival point")
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
        pos : Int,
        is_new : Boolean){

        mPosition=pos
        var mCommute : Commute = commutesList!!.commutesItemsList[mPosition]
        /*routeName = name
        routeStart = start
        routeArrival = arrival
        routeArrivalTime = arrival_time*/
        mSender = sender
        /*mCommute.name = routeName
        mCommute.start = routeStart
        mCommute.arrival = routeArrival
        mCommute.arrival_time_long = routeArrivalTime*/
        isNew = is_new

        if(mCommute.arrival_time_long != "Now"){
            routeArrivalTimeUTC =
                (Timestamp.valueOf(mCommute.arrival_time_long).time/1000)
            mCommute.arrival_time_UTC = routeArrivalTimeUTC
        }

        responseReceived = 0
        sendHTTP(mCommute)
    }

    fun sendHTTP(mCommute : Commute){
        var start_time : String = "now"
        if(mCommute.arrival_time_long != "Now"){
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


        readJSON(json)
        //var mCommute : Commute = commutesList!!.commutesItemsList[mPosition]
        /*mCommute.name = routeName
        mCommute.start = routeStart
        mCommute.arrival= routeArrival
        mCommute.start_time_UTC = routeStartTimeUTC
        mCommute.arrival_time_long = routeArrivalTime
        mCommute.arrival_time_UTC = routeArrivalTimeUTC*/

        if(commutesList!!.commutesItemsList[mPosition].arrival_time_long != "Now"){
            when(responseReceived){
                0 -> {
                    responseReceived = 1
                    responseReceivedMAX = 5
                    commutesList!!.commutesItemsList[mPosition].arrival_time_UTC = routeArrivalTimeUTC

                    routeStartTimeUTC = (commutesList!!.commutesItemsList[mPosition].arrival_time_UTC!! -
                            commutesList!!.commutesItemsList[mPosition].duration_val!!)
                    commutesList!!.commutesItemsList[mPosition].start_time_UTC = routeStartTimeUTC
                    sendHTTP(commutesList!!.commutesItemsList[mPosition])
                }
                1 -> {
                    responseReceivedMAX = responseReceivedMAX - 1

                    commutesList!!.commutesItemsList[mPosition].errorTraffic =
                        (commutesList!!.commutesItemsList[mPosition].start_time_UTC!! +
                                commutesList!!.commutesItemsList[mPosition].duration_traffic_val!! -
                                routeArrivalTimeUTC!!)

                    if(((commutesList!!.commutesItemsList[mPosition].errorTraffic!! > errorLimitUp) or
                                (commutesList!!.commutesItemsList[mPosition].errorTraffic!! < errorLimitDown))
                        and (responseReceived > 0)){
                        routeStartTimeUTC = (commutesList!!.commutesItemsList[mPosition].start_time_UTC!! -
                                commutesList!!.commutesItemsList[mPosition].errorTraffic!!)
                        commutesList!!.commutesItemsList[mPosition].start_time_UTC = routeStartTimeUTC
                        sendHTTP(commutesList!!.commutesItemsList[mPosition])
                    } else{
                        val jdf =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        jdf.setTimeZone(TimeZone.getTimeZone("GMT+1"))
                        commutesList!!.commutesItemsList[mPosition].start_time_long =
                            jdf.format(commutesList!!.commutesItemsList[mPosition].start_time_UTC!! * 1000)

                        dateTime = commutesList!!.commutesItemsList[mPosition].start_time_long.split(" ")
                        date = dateTime[0].split("-")
                        time = dateTime[1].split(":")
                        commutesList!!.commutesItemsList[mPosition].start_time_short =
                            "on " + date[2] +"."+ date[1] +"." + date[0] +
                                    ", at " + time[0] + ":" + time[1]

                        //commutesList!!.commutesItemsList[mPosition].name = routeName
                        commutesList!!.commutesItemsList[mPosition].arrival_time_UTC = routeArrivalTimeUTC
                        if(mSender == "Service"){
                            mService!!.routeRequestedReady(commutesList!!.commutesItemsList[mPosition])
                        }
                        else if(mSender == "Activity"){
                            mActivity!!.routeRequestedReady(mPosition,isNew!!)
                        }
                        responseReceived = 0
                    }
                }
            }
        }
        else mService!!.routeRequestedReady(commutesList!!.commutesItemsList[mPosition])
    }

    fun readJSON(json : String) {
        //var mCommute : Commute = commutesList!!.commutesItemsList[mPosition]
        /*mCommute.name = routeName
        mCommute.start = routeStart
        mCommute.arrival = routeArrival*/

        //mCommute.arrival_time_long = routeArrivalTime
        if (routeArrivalTime!="Now") {
            dateTime = commutesList!!.commutesItemsList[mPosition].arrival_time_long.split(" ")
            date = dateTime[0].split("-")
            time = dateTime[1].split(":")
            commutesList!!.commutesItemsList[mPosition].arrival_time_short =
                "on " + date[2] + "." + date[1] + "." + date[0] +
                        ", at " + time[0] + ":" + time[1]
        }
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

        commutesList!!.commutesItemsList[mPosition].start_address = legs
            .getJSONObject(0)
            .getString("start_address")
        commutesList!!.commutesItemsList[mPosition].start_address_LatLng =
            LatLng(
                startLocationLat.toDouble(),
                startLocationLng.toDouble())

        commutesList!!.commutesItemsList[mPosition].arrival_address = legs
            .getJSONObject(0)
            .getString("end_address")
        commutesList!!.commutesItemsList[mPosition].arrival_address_LatLng =
            LatLng(
                arrivalLocationLat.toDouble(),
                arrivalLocationLng.toDouble())

        commutesList!!.commutesItemsList[mPosition].distance = legs
            .getJSONObject(0)
            .getJSONObject("distance")
            .getString("text")
        commutesList!!.commutesItemsList[mPosition].duration = legs
            .getJSONObject(0)
            .getJSONObject("duration")
            .getString("text")
        commutesList!!.commutesItemsList[mPosition].duration_val = legs
            .getJSONObject(0)
            .getJSONObject("duration")
            .getString("value").toLong()

        if(legs.getJSONObject(0).has("duration_in_traffic")){
            commutesList!!.commutesItemsList[mPosition].duration_traffic = legs
                .getJSONObject(0)
                .getJSONObject("duration_in_traffic")
                .getString("text")
            commutesList!!.commutesItemsList[mPosition].duration_traffic_val = legs
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

        commutesList!!.commutesItemsList[mPosition].path = path

        commutesList!!.commutesItemsList[mPosition].raw_data = json
            .substringBefore("steps")
            .substringAfter("legs")

        //return mCommute
    }

}