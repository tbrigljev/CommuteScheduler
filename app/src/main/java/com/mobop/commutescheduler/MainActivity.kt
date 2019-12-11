package com.mobop.commutescheduler

/* *************************************************************** */
/* HES-SO Master Mobile Operating Systems and Applications ******* */
/* Final project: Commute scheduler ****************************** */
/* Teo Brigljevic, Funda Cubuk, Antonio Gonzalez Puertas ********* */
/* Autumn 2019 *************************************************** */
/* *************************************************************** */

/* Import ******************************************************** */
import android.os.Bundle
import android.widget.Button
import android.graphics.Color
import android.widget.TextView
import android.widget.CheckBox
import android.text.method.ScrollingMovementMethod

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.textfield.TextInputEditText
/* *************************************************************** */

/* Global variables ********************************************** */
const val MAP = 1
const val COMMUTES = 2
const val EDIT = 3

var commutesList : CommutesItemsList? = null
/* *************************************************************** */

/* *************************************************************** */
/* MainActivity ************************************************** */
/* *************************************************************** */
class MainActivity :
    FragmentMap.OnFragmentInteractionListener,
    FragmentCommutes.OnFragmentInteractionListener,
    FragmentEdit.OnFragmentInteractionListener,
    FragmentQuick.OnFragmentInteractionListener,
    FragmentSettings.OnFragmentInteractionListener,
    AppCompatActivity(){

    private var mFragmentManager = supportFragmentManager

    var homeFragment = FragmentHome()
    var commutesFragment = FragmentCommutes(0)
    var mapFragment = FragmentMap(0)
    var editFragment = FragmentEdit()

    companion object{
        var commutes : Commutes? = null
        var start_time : String = "Now"
        lateinit var GoogleKey:String
        var mGoogleAPI : GoogleAPI?=null
    }

    // var mResult : TextView? = null,
    var mTextArrival : TextView? = null
    var mTextDistance : TextView? = null
    var mTextDuration : TextView? = null
    var mTextDurationTraffic : TextView? = null
    var mTextDeparture : TextView? = null
    var mTextDate : TextInputEditText? = null

    /* *********************************************************** */
    /* onCreate() ************************************************ */
    /* Main function ********************************************* */
    /* *********************************************************** */
    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        commutesList = CommutesItemsList()

        mFragmentManager.beginTransaction()
            .add(R.id.main_container_fragments, homeFragment)
            .commit()

        /*
        // mResult = findViewById<TextView>(R.id.result)
        mTextArrival = findViewById(R.id.text_arrival)
        mTextDistance = findViewById(R.id.text_distance)
        mTextDuration = findViewById(R.id.text_duration)
        mTextDurationTraffic = findViewById(R.id.text_duration_traffic)
        mTextDeparture = findViewById(R.id.text_departure)
        mTextDate = findViewById(R.id.text_date)
        // result!!.setMovementMethod(ScrollingMovementMethod())





        /* Create a singleton of the class Commutes for the list * */
        /* *** of commutes and database instance ***************** */
        commutes = Commutes.getSingleton(this)
*/
        /* This object contains all the methods fo using the ***** */
        /* *** Google API **************************************** */
        GoogleKey = getString(R.string.GoogleMapsKey)
        mGoogleAPI = GoogleAPI(this)

    }

    /* *********************************************************** */
    /* routeRequestReady() *************************************** */
    /* Function that receives the result of the Google request *** */
    /* already formatted ***************************************** */
    fun routeRequestedReady(mCommute : Commute){
//        mTextArrival!!.text = mCommute.arrival_time
//        mTextDistance!!.text = mCommute.distance
//        mTextDuration!!.text = mCommute.duration
//        mTextDurationTraffic!!.text = mCommute.duration_traffic
//        mTextDeparture!!.text = mCommute.start_time
//        // result!!.text = mCommute.raw_data

        var builder : LatLngBounds.Builder = LatLngBounds.Builder()
        builder.include(mCommute.start_address_LatLng)
        builder.include(mCommute.arrival_address_LatLng)

        var bounds : LatLngBounds = builder.build()

        FragmentMap.mMap.clear()

        /* Creating the start and arrival markers *************** */
        FragmentMap.mMap.addMarker(MarkerOptions()
            .position(mCommute.start_address_LatLng!!)
            .title(mCommute.start_address))
        FragmentMap.mMap.addMarker(MarkerOptions()
            .position(mCommute.arrival_address_LatLng!!)
            .title(mCommute.arrival_address))
        FragmentMap.mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60))

        /* Drawing the segments of the commute on the map ******** */
        for(i in 0 until mCommute.path.size){
            if(PolylineOptions()
                    .addAll(mCommute.path[i])
                    .color(Color.RED) != null){
                var polyLineOptions = PolylineOptions()
                    .addAll(mCommute.path[i])
                    .color(Color.BLUE)
                    .width(10.toFloat())
                FragmentMap.mMap.addPolyline(polyLineOptions)
            }
        }
    }



    /* *********************************************************** */
    /* onFragmentInteraction() *********************************** */
    /* Fragment-based actions ************************************ */
    /* *********************************************************** */
    override fun onFragmentInteraction(
        fragmentCaller : Int,
        fragmentState : Int){

        when (fragmentCaller){
            MAP -> {
                when (fragmentState){
                    0 -> {
                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                mapFragment,
                                "map"
                            )
                            .addToBackStack("map")
                            .commit()
                    }
                    1 -> {
                        supportFragmentManager.popBackStack()
                    }
                    2 -> {
                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                editFragment,
                                "edit"
                            )
                            .addToBackStack("edit")
                            .commit()
                    }
                }
            }
            COMMUTES -> {
                when (fragmentState){
                    0 -> {
                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                commutesFragment,
                                "commutes"
                            )
                            .addToBackStack("commutes")
                            .commit()
                    }
                    1 -> {
                        supportFragmentManager.popBackStack()
                    }
                    2 -> {
                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                editFragment,
                                "edit"
                            )
                            .addToBackStack("edit")
                            .commit()
                    }
                }
            }
            EDIT -> {
                when(fragmentState){
                    0 -> {
                        supportFragmentManager.popBackStack()
                    }
                }
            }
        }
    }
}
