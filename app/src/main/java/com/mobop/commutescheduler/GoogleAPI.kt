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

class GoogleAPI{

    /* Google Places ********************************************* */
    private lateinit var placesClient : PlacesClient

    private var responseReceived = 0
    private var responseReceivedMAX = 5
    //var GoogleKey = R.string.GoogleMapsKey
    private var mFragmentEdit : FragmentCommutesEdit? = null
    private var mFragmentFavoritesEdit : FragmentFavoritesEdit? = null
    private var mActivity: MainActivity?= null //activity
    private var mContext : Context? = null
    private var mService : NotificationService? = null
    private var mSender : String? = null
    private var mPosition : Int = -1
    private var isNew : Boolean? = null
    private var routeName : String = ""
    private var routeStart : String = ""
    private var routeArrival : String = ""
    private var routeArrivalTime : String = ""
    private var routeArrivalTimeUTC : Long? = null
    private var routeStartTimeUTC : Long? = null
    private val errorLimitUp = 60
    private val errorLimitDown = -300

    private lateinit var dateTime : List<String>
    private lateinit var date : List<String>
    private lateinit var time : List<String>


    fun setActivityContext(activity : FragmentCommutesEdit, context: Context){
        mFragmentEdit = activity
        mContext = context
        setupPlacesAutocomplete()
    }

    fun setActivityContext(activity : FragmentFavoritesEdit, context: Context){
        mFragmentFavoritesEdit = activity
        mContext = context
        setupPlacesAutocompleteFav()
    }
    fun setActivity(activity : MainActivity){
        mActivity = activity
    }

    fun setService(activity : NotificationService){
        mService = activity
    }

    init{}

    private fun setupPlacesAutocomplete(){
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS
        )

        Places.initialize(mContext!!, MainActivity.GoogleKey)
        placesClient = Places.createClient(mContext!!)

        val autocompleteFragmentStart = mFragmentEdit!!
            .childFragmentManager
            .findFragmentById(R.id.fragment_start)
                as AutocompleteSupportFragment

        autocompleteFragmentStart
            .setPlaceFields(placeFields)
        autocompleteFragmentStart
            //.setHint("Set the start point")
            .setOnPlaceSelectedListener(
                object : PlaceSelectionListener{
                    override fun onPlaceSelected(p0 : Place){
                        FragmentCommutesEdit.start_address = p0.address!!
                        // getPhotoAndDetail(p0.id!!.true)
                    }
                    override fun onError(p0 : Status){}
                }
            )

        val autocompleteFragmentArrival = mFragmentEdit!!
            .childFragmentManager
            .findFragmentById(R.id.fragment_arrival)
                as AutocompleteSupportFragment
        autocompleteFragmentArrival
            .setPlaceFields(placeFields)
        autocompleteFragmentArrival
            //.setHint("Set the arrival point")
            .setOnPlaceSelectedListener(
                object : PlaceSelectionListener{
                    override fun onPlaceSelected(p0 : Place){
                        FragmentCommutesEdit.arrival_address = p0.address!!
                        // getPhotoAndDetail(p0.id!!.true)
                    }
                    override fun onError(p0 : Status){}
                }
            )
    }

    private fun setupPlacesAutocompleteFav(){
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS
        )

        Places.initialize(mContext!!, MainActivity.GoogleKey)
        placesClient = Places.createClient(mContext!!)

        val autocompleteFragmentStart = mFragmentFavoritesEdit!!
            .childFragmentManager
            .findFragmentById(R.id.fragment_address)
                as AutocompleteSupportFragment

        autocompleteFragmentStart
            .setPlaceFields(placeFields)
        autocompleteFragmentStart
            //.setHint("Set the start point")
            .setOnPlaceSelectedListener(
                object : PlaceSelectionListener{
                    override fun onPlaceSelected(p0 : Place){
                        FragmentFavoritesEdit.address = p0.address!!
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
        val mCommute : Commute = commutesList!!.commutesItemsList[mPosition]

        mSender = sender
        isNew = is_new

        if(mCommute.arrival_time_long != "Now"){
            routeArrivalTimeUTC =
                (Timestamp.valueOf(mCommute.arrival_time_long).time/1000)
            mCommute.arrival_time_UTC = routeArrivalTimeUTC
        }

        responseReceived = 0
        sendHTTP(mCommute)
    }

    fun requestRouteService(
        origin : String,
        destination : String){

        mSender = "Service"
        isNew = false
        val mCommute = Commute()
        mCommute.start_address = origin
        mCommute.arrival_address = destination
        mCommute.arrival_time_long="Now"
        routeArrivalTime="Now"

        responseReceived = 0
        sendHTTP(mCommute)
    }

    private fun sendHTTP(mCommute : Commute){
        var startTime = "now"
        if(mCommute.arrival_time_long != "Now"){
            startTime = mCommute.arrival_time_UTC.toString()
        }

        val mRoute =
            "https://maps.googleapis.com/maps/" +
                    "api/directions/json?origin=" + mCommute.start_address +
                    "&destination=" + mCommute.arrival_address +
                    "&departure_time=" + startTime +
                    "&traffic_model=best_guess" +
                    "&key=" + MainActivity.GoogleKey

        val httpRequest = HTTPRequest()
        httpRequest.setActivityContext(this)
        httpRequest.execute(mRoute)
    }

    fun getResults(json : String){

        val mCommute : Commute = readJSON(json)

        if((mCommute.arrival_time_long != "Now")and (mSender!="Service")){
            mCommute.start_address = commutesList!!.commutesItemsList[mPosition].start_address
            mCommute.arrival_address = commutesList!!.commutesItemsList[mPosition].arrival_address
            mCommute.arrival_time_long = commutesList!!.commutesItemsList[mPosition].arrival_time_long
            mCommute.start_time_UTC= routeStartTimeUTC
            mCommute.arrival_time_UTC= routeArrivalTimeUTC
            when(responseReceived){
                0 -> {
                    responseReceived = 1
                    responseReceivedMAX = 5
                    mCommute.arrival_time_UTC = routeArrivalTimeUTC

                    routeStartTimeUTC = (mCommute.arrival_time_UTC!! -
                            mCommute.duration_val!!)
                    mCommute.start_time_UTC = routeStartTimeUTC
                    sendHTTP(mCommute)
                }
                1 -> {
                    responseReceivedMAX -= 1

                    mCommute.errorTraffic =
                        (mCommute.start_time_UTC!! +
                                mCommute.duration_traffic_val!! -
                                routeArrivalTimeUTC!!)

                    if(((mCommute.errorTraffic!! > errorLimitUp) or
                                (mCommute.errorTraffic!! < errorLimitDown))
                        and (responseReceived > 0)){
                        routeStartTimeUTC = (mCommute.start_time_UTC!! -
                                mCommute.errorTraffic!!)
                        mCommute.start_time_UTC = routeStartTimeUTC
                        sendHTTP(mCommute)
                    } else{
                        val jdf =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        jdf.timeZone = TimeZone.getTimeZone("GMT+1")
                        mCommute.start_time_long =
                            jdf.format(mCommute.start_time_UTC!! * 1000)

                        dateTime = mCommute.start_time_long.split(" ")
                        date = dateTime[0].split("-")
                        time = dateTime[1].split(":")
                        mCommute.start_time_short =
                            "on " + date[2] +"."+ date[1] +"." + date[0] +
                                    ", at " + time[0] + ":" + time[1]

                        //commutesList!!.commutesItemsList[mPosition].name = routeName
                        mCommute.arrival_time_UTC = routeArrivalTimeUTC
                        if(mSender == "Service"){
                            mService!!.routeRequestedReady(mCommute)
                        }
                        else if(mSender == "Activity"){

                            commutesList!!
                                .commutesItemsList[mPosition].start_address =
                                mCommute.start_address
                            commutesList!!
                                .commutesItemsList[mPosition].start_address_LatLng =
                                mCommute.start_address_LatLng
                            commutesList!!
                                .commutesItemsList[mPosition].arrival_address =
                                mCommute.arrival_address
                            commutesList!!
                                .commutesItemsList[mPosition].arrival_address_LatLng =
                                mCommute.arrival_address_LatLng
                            commutesList!!
                                .commutesItemsList[mPosition].distance =
                                mCommute.distance
                            commutesList!!
                                .commutesItemsList[mPosition].duration =
                                mCommute.duration
                            commutesList!!
                                .commutesItemsList[mPosition].duration_val =
                                mCommute.duration_val
                            commutesList!!
                                .commutesItemsList[mPosition].duration_traffic =
                                mCommute.duration_traffic
                            commutesList!!
                                .commutesItemsList[mPosition].duration_traffic_val =
                                mCommute.duration_traffic_val
                            commutesList!!
                                .commutesItemsList[mPosition].path =
                                mCommute.path
                            commutesList!!
                                .commutesItemsList[mPosition].raw_data =
                                mCommute.raw_data
                            commutesList!!
                                .commutesItemsList[mPosition].arrival_time_UTC =
                                mCommute.arrival_time_UTC
                            commutesList!!
                                .commutesItemsList[mPosition].start_time_UTC =
                                mCommute.start_time_UTC
                            commutesList!!
                                .commutesItemsList[mPosition].errorTraffic =
                                mCommute.errorTraffic
                            commutesList!!
                                .commutesItemsList[mPosition].start_time_long =
                                mCommute.start_time_long
                            commutesList!!
                                .commutesItemsList[mPosition].start_time_short =
                                mCommute.start_time_short

                            mActivity!!.routeRequestedReady(mPosition,isNew!!)
                        }
                        responseReceived = 0
                    }
                }
            }
        }
        else mService!!.routeRequestedReady(mCommute)
    }

    private fun readJSON(json : String) : Commute {
        val mCommute = Commute()

        if (routeArrivalTime!="Now") {
            dateTime = commutesList!!.commutesItemsList[mPosition].arrival_time_long.split(" ")
            date = dateTime[0].split("-")
            time = dateTime[1].split(":")
            mCommute.arrival_time_short =
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