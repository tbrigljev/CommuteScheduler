package com.mobop.commutescheduler

/* ****************************************************************** */
/* HES-SO Master Mobile Operating Systems and Applications ********** */
/* Final project: Commute scheduler, Layouts ************************ */
/* Teo Brigljevic *************************************************** */
/* Autumn 2019 ****************************************************** */
/* ****************************************************************** */

/* Import *********************************************************** */
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/* Global variables ************************************************* */
const val MAP = 1
const val COMMUTES = 2
const val QUICK = 3

var commutesList : CommutesItemsList? = null

/* ****************************************************************** */
/* MainActivity ***************************************************** */
/* ****************************************************************** */
class MainActivity :
    FragmentMap.OnFragmentInteractionListener,
    FragmentCommutesRecycler.OnFragmentInteractionListener,
    FragmentQuick.OnFragmentInteractionListener,
    FragmentSettings.OnFragmentInteractionListener,
    AppCompatActivity(){

    private var mFragmentManager = supportFragmentManager

    var homeFragment = FragmentHome()
    var commutesFragment = FragmentCommutesRecycler(0)
    var mapFragment = FragmentMap(0)

    /* ************************************************************** */
    /* onCreate() *************************************************** */
    /* Main function ************************************************ */
    /* ************************************************************** */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        commutesList = CommutesItemsList()

        /*
        mFragmentManager.beginTransaction()
            .add(R.id.main_container_fragments, mapFragment)
            .commit()*/

        /*
        mFragmentManager.beginTransaction()
        .add(R.id.main_container_fragments, commutesFragment)
        .commit()*/


        mFragmentManager.beginTransaction()
            .add(R.id.main_container_fragments, homeFragment)
            .commit()
    }

    /* ************************************************************** */
    /* onFragmentInteraction() ************************************** */
    /* Fragment-based actions *************************************** */
    /* ************************************************************** */
    override fun onFragmentInteraction(fragmentCaller : Int, fragmentState : Int) {

        when (fragmentCaller) {
            MAP -> {
                when (fragmentState) {
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
                when (fragmentState) {
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
