package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
/* *************************************************************** */

/* FragmentHome ************************************************** */
/* Shows up when launching the app, combines the map, commutes and */
/* quick-route on one screen ************************************* */

class FragmentHome : Fragment(){

    companion object {
        var commutesFragment = FragmentCommutes(1)
        var mapFragment = FragmentMap(1)
        var quickFragment = FragmentShortcuts()
    }

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.activity_main_home,
            container,
            false)

        val mFragmentManager = childFragmentManager

        mFragmentManager.beginTransaction()
            .replace(R.id.main_container_map, mapFragment)
            .commit()

        mFragmentManager.beginTransaction()
            .replace(R.id.main_container_commutes, commutesFragment,"list")
            .commit()

        mFragmentManager.beginTransaction()
            .replace(R.id.main_container_shortcuts, quickFragment)
            .commit()

        return view
    }

    override fun onAttach(context : Context){
        super.onAttach(context)
    }

    override fun onDetach(){
        super.onDetach()
    }
}