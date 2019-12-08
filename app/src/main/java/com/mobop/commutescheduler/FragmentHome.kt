package com.mobop.commutescheduler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentHome :
    Fragment(){

    var commutesFragment = FragmentCommutes(1)
    var mapFragment = FragmentMap(1)

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savecInstanceState : Bundle?) : View? {

        val view = inflater.inflate(R.layout.activity_main_home, container, false)

        val mFragmentManager = childFragmentManager

        mFragmentManager.beginTransaction()
            .replace(R.id.main_container_map, mapFragment)
            .commit()

        mFragmentManager.beginTransaction()
            .add(R.id.main_container_commutes, commutesFragment)
            .commit()

        return view
    }

}