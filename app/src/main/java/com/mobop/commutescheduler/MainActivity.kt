package com.mobop.commutescheduler

/* *************************************************************** */
/* HES-SO Master Mobile Operating Systems and Applications ******* */
/* Final project: Commute scheduler ****************************** */
/* Teo Brigljevic, Funda Cubuk, Antonio Gonzalez Puertas ********* */
/* Autumn 2019 *************************************************** */
/* *************************************************************** */

/* Import ******************************************************** */

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.textfield.TextInputEditText
import com.mobop.commutescheduler.FragmentCommutes.Companion.mRecyclerView


/* *************************************************************** */

/* Global variables ********************************************** */
const val MAIN = 0
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
    FragmentShortcuts.OnFragmentInteractionListener,
    FragmentSettings.OnFragmentInteractionListener,
    AppCompatActivity(){

    private var mFragmentManager = supportFragmentManager

    var homeFragment = FragmentHome()
    var commutesFragment = FragmentCommutes(0)
    var mapFragment = FragmentMap(0)
    var newFragment = FragmentEdit(true, -1)
    lateinit var editFragment : FragmentEdit
    lateinit var previousTitle : String
    lateinit var settings_item : MenuItem

    companion object{
        var commutes : Commutes? = null
        var start_time : String = "Now"
        lateinit var GoogleKey : String
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
            .add(R.id.main_container_fragments, homeFragment,"home")
            .commit()

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        toolbar.title = getString(R.string.name_main)
        previousTitle = getString(R.string.name_main)

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

    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        settings_item = menu.findItem(R.id.action_settings)
        return true
    }

    /* *********************************************************** */
    /* onFragmentInteraction() *********************************** */
    /* Fragment-based actions ************************************ */
    /* *********************************************************** */
    override fun onFragmentInteraction(
        fragmentCaller : Int,
        fragmentState : IntArray){

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        when (fragmentCaller){
            MAIN -> {
                when(fragmentState[0]){
                    1 -> {
                        val text = "No favorites implemented"
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(this, text, duration)
                        toast.show()
                    }
                    2 -> {
                        val text = "No quick selection implemented"
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(this, text, duration)
                        toast.show()
                    }
                    3 -> {
                        toolbar.title = getString(R.string.name_new)
                        previousTitle = getString(R.string.name_main)
                        /* HIDE SETTINGS IN EDITION FRAGMENT
                           NOT WORKING
                         */
                        settings_item.isVisible = false
                        settings_item.isEnabled = false

                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                newFragment,
                                "new"
                            )
                            .addToBackStack("new")
                            .commit()
                    }
                }
            }
            MAP -> {
                var dividerTop : View = findViewById(R.id.main_divider_top)
                var containerCommutes : FrameLayout = findViewById(R.id.main_container_commutes)
                var dividerBottom : View = findViewById(R.id.main_divider_bottom)
                var containerQuick : FrameLayout = findViewById(R.id.main_container_shortcuts)

                var enhanceMapButton : ImageButton = findViewById(R.id.map_button_enhance)
                var addMapButton : ImageButton = findViewById(R.id.map_button_add)
                var returnMapButton : ImageButton =  findViewById(R.id.map_button_return)

                when (fragmentState[0]){
                    0 -> {
                        toolbar.visibility = View.GONE

                        toolbar.title = getString(R.string.name_map)
                        previousTitle = getString(R.string.name_main)

                        dividerTop.visibility = View.GONE
                        containerCommutes.visibility = View.GONE
                        dividerBottom.visibility = View.GONE
                        containerQuick.visibility = View.GONE
                    }
                    1 -> {
                        toolbar.visibility = View.VISIBLE

                        toolbar.title = getString(R.string.name_main)
                        previousTitle = getString(R.string.name_map)

                        enhanceMapButton.visibility = View.VISIBLE
                        addMapButton.visibility = View.GONE
                        returnMapButton.visibility = View.GONE

                        dividerTop.visibility = View.VISIBLE
                        containerCommutes.visibility = View.VISIBLE
                        dividerBottom.visibility = View.VISIBLE
                        containerQuick.visibility = View.VISIBLE
                    }
                    2 -> {
                        toolbar.visibility = View.VISIBLE

                        toolbar.title = getString(R.string.name_new)
                        previousTitle = getString(R.string.name_map)
                        /* HIDE SETTINGS IN EDITION FRAGMENT
                           NOT WORKING
                         */
                        settings_item.isVisible = false
                        settings_item.isEnabled = false

                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                newFragment,
                                "new"
                            )
                            .addToBackStack("new")
                            .commit()
                    }
                }
            }
            COMMUTES -> {
                when (fragmentState[0]){
                    0 -> {
                        toolbar.title = getString(R.string.name_commutes)
                        previousTitle = getString(R.string.name_main)

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
                        toolbar.title = getString(R.string.name_main)
                        previousTitle = getString(R.string.name_main)


                        supportFragmentManager.popBackStack()
                    }
                    2 -> {
                        toolbar.title = getString(R.string.name_new )
                        previousTitle = getString(R.string.name_commutes)
                        /* HIDE SETTINGS IN EDITION FRAGMENT
                           NOT WORKING
                         */
                        settings_item.isVisible = false
                        settings_item.isEnabled = false

                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                newFragment,
                                "new"
                            )
                            .addToBackStack("new")
                            .commit()
                    }
                    3 -> {
                        previousTitle = toolbar.title.toString()
                        toolbar.title = getString(R.string.name_edit)
                        /* HIDE SETTINGS IN EDITION FRAGMENT
                           NOT WORKING
                         */
                        settings_item.isVisible = false
                        settings_item.isEnabled = false

                        editFragment = FragmentEdit(false, fragmentState[1])

                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                editFragment,
                                "edit"
                            )
                            .addToBackStack("edit")
                            .commit()
                    }
                    /*4 -> {
                        commutesList!!.commutesItemsList.removeAt(fragmentState[1])
                        val text = "Commute deleted"
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(this, text, duration)
                        toast.show()
                    }*/
                }
            }
            EDIT -> {
                when(previousTitle){
                    getString(R.string.name_map) -> {
                        toolbar.visibility = View.GONE

                        toolbar.title = getString(R.string.name_map)
                        previousTitle = getString(R.string.name_new)
                        settings_item.isVisible = true
                        settings_item.isEnabled = true
                    }
                    else -> {
                        toolbar.visibility = View.VISIBLE

                        toolbar.title = previousTitle
                        previousTitle = getString(R.string.name_main)
                        settings_item.isVisible = true
                        settings_item.isEnabled = true
                    }
                }
                supportFragmentManager.popBackStack()
               /* var fragment: FragmentHome? = supportFragmentManager.findFragmentByTag("home") as FragmentHome
                var fragment_commutes: FragmentCommutes? = fragment!!.childFragmentManager .findFragmentByTag("list") as FragmentCommutes
                fragment_commutes!!.mAdapter!!.viewLayoutButtons(false,fragmentState[1])*/
            }
        }
    }

    override fun onBackPressed() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        when(toolbar.title){
            getString(R.string.name_map) -> {
                onFragmentInteraction(1,intArrayOf(1,0))
            }
            getString(R.string.name_new) -> {
                onFragmentInteraction(3,intArrayOf(0,0))
                }
            else -> {
                toolbar.title = previousTitle
                previousTitle = getString(R.string.name_main)
                super.onBackPressed()
            }
        }

//        when(toolbar.visibility){
//            View.VISIBLE -> {
//                toolbar.title = previousTitle
//                previousTitle = getString(R.string.name_main)
//                super.onBackPressed()
//            }
//            View.GONE -> {
//                onFragmentInteraction(0,1)
//            }
//        }
    }
}
