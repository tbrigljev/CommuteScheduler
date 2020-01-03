package com.mobop.commutescheduler

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class FragmentSettings : Fragment(){

    private var mListener : OnFragmentInteractionListener? = null

    private val fragmentID = 99
    private var source : IntArray = intArrayOf(0,0)

    private lateinit var returnSettingsButton : ImageButton

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savecInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.fragment_settings,
            container,
            false
        )

        returnSettingsButton =
            view.findViewById(R.id.settings_button_return)
                    as ImageButton

        returnSettingsButton.setOnClickListener{
            doSettingsReturn(fragmentID)
        }

        return view
    }

    override fun onAttach(context : Context){
        super.onAttach(context)
        if(context is OnFragmentInteractionListener){
            mListener = context
        } else{
            throw RuntimeException(context!!.toString() +
                    " must implement OnFragmentInteractionListener")
        }
    }
    override fun onDetach(){
        super.onDetach()
        mListener = null
    }

    private fun doSettingsReturn(fragmentCaller : Int){
        if (mListener != null){
            source[0] = 0
            mListener!!
                .onFragmentInteraction(fragmentCaller, source)
        }
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : IntArray)
    }

}