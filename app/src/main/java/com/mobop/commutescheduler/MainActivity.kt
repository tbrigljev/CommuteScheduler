package com.mobop.commutescheduler

/* *************************************************************** */
/* HES-SO Master Mobile Operating Systems and Applications ******* */
/* Final project: Commute scheduler ****************************** */
/* Teo BRIGLJEVIC, Funda CUBUK, Antonio GONZALEZ PUERTAS ********* */
/* Autumn 2019 *************************************************** */
/* *************************************************************** */

/* Import ******************************************************** */
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import org.jetbrains.anko.doAsync
/* *************************************************************** */

/* Global variables ********************************************** */
const val MAIN = 0
const val MAP = 1
const val COMMUTES = 2
const val EDIT = 3
const val FAVORITES = 4
const val FAVORITESEDIT = 5
const val QUICK = 6
const val SETTINGS = 99

var commutesList : CommutesItemsList? = null
/* *************************************************************** */

/* *************************************************************** */
/* MainActivity ************************************************** */
/* *************************************************************** */
class MainActivity :
    FragmentMap.OnFragmentInteractionListener,
    FragmentCommutes.OnFragmentInteractionListener,
    FragmentCommutesEdit.OnFragmentInteractionListener,
    FragmentShortcuts.OnFragmentInteractionListener,
    FragmentFavorites.OnFragmentInteractionListener,
    FragmentFavoritesEdit.OnFragmentInteractionListener,
    FragmentSettings.OnFragmentInteractionListener,
    AppCompatActivity(){

    private var mFragmentManager = supportFragmentManager

    private var homeFragment = FragmentHome()
    private var commutesFragment = FragmentCommutes(0)
    private var mapFragment = FragmentMap(0)
    private var newFragment = FragmentCommutesEdit(true, -1)
    private var favoritesFragment = FragmentFavorites()
    private var favoriteEditFragment = FragmentFavoritesEdit(true, -1)
    private var quickFragment = FragmentQuick()
    private var settingsFragment = FragmentSettings()
    private lateinit var editFragment : FragmentCommutesEdit
    private lateinit var previousTitle : String
    private lateinit var settingsItem : MenuItem
    private var mState: Int=1

    companion object{
        var commutes : Commute? = null
        var startTime : String = "Now"
        lateinit var GoogleKey : String
        var mGoogleAPI : GoogleAPI?=null
    }

    /* *********************************************************** */
    /* onCreate() ************************************************ */
    /* Main function ********************************************* */
    /* *********************************************************** */
    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGoogleAPI = GoogleAPI()
        mGoogleAPI!!.setActivity(this)

        mFragmentManager.beginTransaction()
            .add(R.id.main_container_fragments, homeFragment,"home")
            .commit()

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        toolbar.title = getString(R.string.name_main)
        previousTitle = getString(R.string.name_main)

        /* Create a singleton of the class Commutes for the list * */
        /* *** of commutes and database instance ***************** */
        commutesList = CommutesItemsList.getSingleton(this)

        /* This object contains all the methods fo using the ***** */
        /* *** Google API **************************************** */
        GoogleKey = getString(R.string.GoogleMapsKey)

    }

    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        settingsItem = menu.findItem(R.id.action_settings)

        if (mState == 0) {
            for (i in 0 until menu.size()) menu.getItem(i).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        if(item.itemId == R.id.action_settings){
            showSettings()
        }
        return true
    }

    private fun showSettings(){
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        previousTitle = toolbar.title.toString()
        toolbar.title = getString(R.string.name_settings)

        mState = 0
        invalidateOptionsMenu()

        mFragmentManager.beginTransaction()
            .add(
                R.id.main_container_fragments,
                settingsFragment,
                "settings"
            )
            .addToBackStack("settings")
            .commit()
    }

    /* *********************************************************** */
    /* routeRequestReady() *************************************** */
    /* Function that receives the result of the Google request *** */
    /* already formatted ***************************************** */

    fun routeRequestedReady(pos : Int, isNew : Boolean){
        val mCommute = commutesList!!.commutesItemsList[pos]
        if (isNew) {
            doAsync{
                var id=commutesList!!.database.insertAll(mCommute)

            }
            //commutesList!!.commutesItemsList.add(mCommute)
            FragmentCommutes.mRecyclerView!!.adapter!!.notifyDataSetChanged()
            val lastPos = commutesList!!.commutesItemsList.size - 1
            FragmentCommutes.mRecyclerView!!.smoothScrollToPosition(lastPos)
            //FragmentCommutes.mAdapter!!.viewLayouts(false,true,lastPos) //Gives an execption ??


            if (mCommute.alarm==true){
                val checkPoint= 15 // 15min
                Notifications().setNotification(mCommute, checkPoint, this@MainActivity)

                /*checkPoint =30 // 30min
                Notifications().setNotification(mCommute, checkPoint, this@MainActivity)

                checkPoint =60 // 1h
                Notifications().setNotification(mCommute, checkPoint, this@MainActivity)*/
            }


        }else{ //Modification
            doAsync{ commutesList!!
                .database
                .updateAll(commutesList!!.commutesItemsList[pos])}
        }

        FragmentCommutes.mRecyclerView!!.adapter!!.notifyDataSetChanged()

        FragmentMap.mapFieldCommuteDuration.visibility = View.VISIBLE
        FragmentMap.mapFieldCommuteDText.visibility = View.VISIBLE
        FragmentMap.mapFieldCommuteName.visibility = View.VISIBLE

        FragmentMap.mapFieldCommuteNText.visibility = View.GONE
        val elementSimpleTime = mCommute.duration_val!!.toInt()/60
        var hoursText = ""
        var minutesText = ""
        if(elementSimpleTime >= 60){
            hoursText = (elementSimpleTime/60).toString() + "h"
            val minutes = elementSimpleTime%60
            if(minutes != 0){
                minutesText = (elementSimpleTime%60).toString() + "min"
            }
        } else {
            minutesText = elementSimpleTime.toString() + "min"
        }
        FragmentMap.mapFieldCommuteDuration.text = hoursText + minutesText
        FragmentMap.mapFieldCommuteDText.text = getString(R.string.text_commute_duration)
        FragmentMap.mapFieldCommuteName.text = mCommute.name

        val startAddress =
            mCommute.start_address.toString().split(", ")
        var startAddressText = ""
        for(startAddressBit in startAddress){
            startAddressText += startAddressBit + "\n"
        }
        FragmentMap.mapOverlayStart.text = startAddressText.trim()

        val startTime =
            mCommute.start_time_short.split(", ")
        var startTimeText = ""
        for(startTimeBit in startTime){
            startTimeText += startTimeBit + "\n"
        }
        FragmentMap.mapOverlayStartTime.text = startTimeText.trim()

        val arrivalAddress =
            mCommute.arrival_address.toString().split(", ")
        var arrivalAddressText = ""
        for (arrivalAddressBit in arrivalAddress){
            arrivalAddressText += arrivalAddressBit + "\n"
        }
        FragmentMap.mapOverlayDestination.text = arrivalAddressText.trim()

        val arrivalTime =
            mCommute.arrival_time_short.split(", ")
        var arrivalTimeText = ""
        for(arrivalTimeBit in arrivalTime){
            arrivalTimeText += arrivalTimeBit + "\n"
        }
        FragmentMap.mapOverlayDestinationTime.text = arrivalTimeText.trim()

        FragmentMap.mapOverlayLength.text = mCommute.distance

        val builder : LatLngBounds.Builder = LatLngBounds.Builder()
        builder.include(mCommute.start_address_LatLng)
        builder.include(mCommute.arrival_address_LatLng)

        val bounds : LatLngBounds = builder.build()

        FragmentMap.mMap.clear()

        /* Creating the start and arrival markers **************** */
        FragmentMap.mMap.addMarker(
            MarkerOptions()
                .position(mCommute.start_address_LatLng!!)
                .title(mCommute.start_address))
        FragmentMap.mMap.addMarker(
            MarkerOptions()
                .position(mCommute.arrival_address_LatLng!!)
                .title(mCommute.arrival_address))
        FragmentMap.mMap.moveCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, 140))

        /* Drawing the segments of the commute on the map ******** */
        for(i in 0 until mCommute.path.size){
            if(PolylineOptions()
                    .addAll(mCommute.path[i])
                    .color(Color.RED) != null){
                val polyLineOptions = PolylineOptions()
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
        fragmentState : IntArray){

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        val containerMap :
                FrameLayout =
            findViewById(R.id.main_container_map)
        val dividerTop :
                View =
            findViewById(R.id.main_divider_top)
        val containerCommutes :
                FrameLayout =
            findViewById(R.id.main_container_commutes)
        val dividerBottom :
                View =
            findViewById(R.id.main_divider_bottom)
        val containerQuick :
                FrameLayout =
            findViewById(R.id.main_container_shortcuts)

        when (fragmentCaller){
            MAIN -> {
                when(fragmentState[0]){
                    1 -> {
                        toolbar.title = getString(R.string.name_favorites)
                        previousTitle = getString(R.string.name_main)

                        mState = 0
                        invalidateOptionsMenu()

                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                favoritesFragment,
                                "favorites"
                            )
                            .addToBackStack("favorites")
                            .commit()
                    }
                    2 -> {
                        toolbar.title = getString(R.string.name_quick)
                        previousTitle = getString(R.string.name_main)

                        mState = 0
                        invalidateOptionsMenu()

                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                quickFragment,
                                "quick"
                            )
                            .addToBackStack("quick")
                            .commit()
                    }
                    3 -> {
                        toolbar.title = getString(R.string.name_new)
                        previousTitle = getString(R.string.name_main)

                        mState = 0
                        invalidateOptionsMenu()

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

                val enhanceMapButton :
                        ImageButton =
                    findViewById(R.id.map_button_enhance)
                val addMapButton :
                        ImageButton =
                    findViewById(R.id.map_button_add)
                val returnMapButton :
                        ImageButton =
                    findViewById(R.id.map_button_return)
                val overlayMap :
                        ConstraintLayout =
                    findViewById(R.id.map_overlay)
                val overlayMapButton :
                        ImageButton =
                    findViewById(R.id.map_button_overlay)
                val textMap :
                        TextView =
                    findViewById(R.id.map_text_commute_name)

                when(fragmentState[0]){
                    0 -> {
                        toolbar.visibility = View.GONE

                        toolbar.title = getString(R.string.name_map)
                        previousTitle = getString(R.string.name_main)

                        if(textMap.visibility == View.GONE){
                            overlayMapButton.visibility = View.VISIBLE
                            overlayMap.visibility = View.VISIBLE
                        }

                        dividerTop.visibility = View.GONE
                        containerCommutes.visibility = View.GONE
                        dividerBottom.visibility = View.GONE
                        containerQuick.visibility = View.GONE
                    }
                    1 -> {
                        toolbar.visibility = View.VISIBLE

                        toolbar.title = getString(R.string.name_main)
                        previousTitle = getString(R.string.name_map)

                        mState = 1
                        invalidateOptionsMenu()

                        enhanceMapButton.visibility = View.VISIBLE
                        addMapButton.visibility = View.GONE
                        returnMapButton.visibility = View.GONE
                        overlayMap.visibility = View.GONE
                        overlayMapButton.visibility = View.GONE

                        dividerTop.visibility = View.VISIBLE
                        containerCommutes.visibility = View.VISIBLE
                        dividerBottom.visibility = View.VISIBLE
                        containerQuick.visibility = View.VISIBLE
                    }
                    2 -> {
                        toolbar.visibility = View.VISIBLE

                        mState = 0
                        invalidateOptionsMenu()

                        toolbar.title = getString(R.string.name_new)
                        previousTitle = getString(R.string.name_map)

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

                val enhanceCommutesButton :
                        ImageButton =
                    findViewById(R.id.commutes_button_enhance)
                val addCommutesButton :
                        ImageButton =
                    findViewById(R.id.commutes_button_add)
                val returnCommutesButton :
                        ImageButton =
                    findViewById(R.id.commutes_button_return)

                when(fragmentState[0]){
                    0 -> {
                        toolbar.title = getString(R.string.name_commutes)
                        previousTitle = getString(R.string.name_main)

                        mState = 0
                        invalidateOptionsMenu()

                        dividerTop.visibility = View.GONE
                        containerMap.visibility = View.GONE
                        dividerBottom.visibility = View.GONE
                        containerQuick.visibility = View.GONE
                    }
                    1 -> {
                        toolbar.title = getString(R.string.name_main)
                        previousTitle = getString(R.string.name_main)

                        mState = 1
                        invalidateOptionsMenu()

                        enhanceCommutesButton.visibility = View.VISIBLE
                        addCommutesButton.visibility = View.GONE
                        returnCommutesButton.visibility = View.GONE

                        dividerTop.visibility = View.VISIBLE
                        containerMap.visibility = View.VISIBLE
                        dividerBottom.visibility = View.VISIBLE
                        containerQuick.visibility = View.VISIBLE
                    }
                    2 -> {
                        toolbar.title = getString(R.string.name_new)
                        previousTitle = getString(R.string.name_commutes)

                        mState = 0
                        invalidateOptionsMenu()

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

                        mState = 0
                        invalidateOptionsMenu()

                        editFragment =
                            FragmentCommutesEdit(false, fragmentState[1])

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

                val emptyCommutes :
                        ConstraintLayout =
                    findViewById(R.id.commutes_empty_container)
                val addCommutesButton :
                        ImageButton =
                    findViewById(R.id.commutes_button_add)
                val overlayMap :
                        ConstraintLayout =
                    findViewById(R.id.map_overlay)

                when(previousTitle){
                    getString(R.string.name_map) -> {
                        toolbar.visibility = View.GONE

                        overlayMap.visibility = View.VISIBLE

                        toolbar.title = getString(R.string.name_map)
                        previousTitle = getString(R.string.name_new)

                        mState = 1
                        invalidateOptionsMenu()
                    }
                    getString(R.string.name_commutes) -> {
                        toolbar.visibility = View.VISIBLE

                        mState = 0
                        invalidateOptionsMenu()

                        toolbar.title = previousTitle
                        previousTitle = getString(R.string.name_main)
                    }
                    else -> {
                        toolbar.visibility = View.VISIBLE

                        mState = 1
                        invalidateOptionsMenu()

                        toolbar.title = previousTitle
                        previousTitle = getString(R.string.name_main)
                    }
                }
                if(commutesList!!.commutesItemsList.count() > 0){
                    emptyCommutes.visibility = View.GONE
                    addCommutesButton.visibility = View.VISIBLE
                }
                supportFragmentManager.popBackStack()
            }
            FAVORITES -> {

                when(fragmentState[0]){
                    0 -> {
                        previousTitle = toolbar.title.toString()
                        toolbar.title = getString(R.string.name_favorite_new)

                        mState = 0
                        invalidateOptionsMenu()

                        favoriteEditFragment =
                            FragmentFavoritesEdit(true, fragmentState[1])

                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                favoriteEditFragment,
                                "new_favorite"
                            )
                            .addToBackStack("new_favorite")
                            .commit()
                    }
                    1 -> {
                        previousTitle = toolbar.title.toString()
                        toolbar.title = getString(R.string.name_favorite_edit)

                        mState = 0
                        invalidateOptionsMenu()

                        favoriteEditFragment =
                            FragmentFavoritesEdit(false, fragmentState[1])

                        mFragmentManager.beginTransaction()
                            .add(
                                R.id.main_container_fragments,
                                favoriteEditFragment,
                                "favorite_edit"
                            )
                            .addToBackStack("favorite_edit")
                            .commit()
                    }
                    2 -> {
                        toolbar.title = getString(R.string.name_main)
                        previousTitle = getString(R.string.name_favorites)

                        mState = 1
                        invalidateOptionsMenu()

                        supportFragmentManager.popBackStack()
                    }
                }
            }
            FAVORITESEDIT -> {
                val emptyFavorites :
                        ConstraintLayout =
                    findViewById(R.id.favorites_empty_container)
                val addFavoritesButton :
                        ImageButton =
                    findViewById(R.id.favorites_button_add)

                previousTitle = toolbar.title.toString()
                toolbar.title = getString(R.string.name_favorites)

                mState = 0
                invalidateOptionsMenu()

                if(commutesList!!.favoritesItemsList.count() > 0){
                    emptyFavorites.visibility = View.GONE
                    addFavoritesButton.visibility = View.VISIBLE
                }
                supportFragmentManager.popBackStack()
            }
            SETTINGS -> {

                previousTitle = toolbar.title.toString()
                toolbar.title = getString(R.string.name_main)

                mState = 1
                invalidateOptionsMenu()
                
                supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onBackPressed(){
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        when(toolbar.title){
            getString(R.string.name_map) -> {
                onFragmentInteraction(1,intArrayOf(1,0))
            }
            getString(R.string.name_commutes) -> {
                onFragmentInteraction(2, intArrayOf(1,0))
            }
            getString(R.string.name_new) -> {
                onFragmentInteraction(3,intArrayOf(0,0))
                }
            getString(R.string.field_favorite_name) -> {
                onFragmentInteraction(4, intArrayOf(2,0))
            } else -> {
                toolbar.title = previousTitle
                previousTitle = getString(R.string.name_main)
                super.onBackPressed()
            }
        }
    }
}
