package com.mobop.commutescheduler

/* *************************************************************** */
/* HES-SO Master Mobile Operating Systems and Applications ******* */
/* Final project: Commute scheduler ****************************** */
/* Teo Brigljevic, Funda Cubuk, Antonio Gonzalez Puertas ********* */
/* Autumn 2019 *************************************************** */
/* *************************************************************** */

/* Import ******************************************************** */
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/* Global variables ********************************************** */
const val MAP = 1
const val COMMUTES = 2
const val QUICK = 3

var commutesList : CommutesItemsList? = null

/* *************************************************************** */
/* MainActivity ************************************************** */
/* *************************************************************** */
class MainActivity :
    FragmentMap.OnFragmentInteractionListener,
    FragmentCommutes.OnFragmentInteractionListener,
    FragmentQuick.OnFragmentInteractionListener,
    FragmentSettings.OnFragmentInteractionListener,
    AppCompatActivity(){

    private var mFragmentManager = supportFragmentManager

    var homeFragment = FragmentHome()
    var commutesFragment = FragmentCommutes(0)
    var mapFragment = FragmentMap(0)

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
                }
            }
        }
    }
}
