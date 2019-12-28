package com.mobop.commutescheduler

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class FragmentQuick : Fragment(){

    var mRecyclerView : RecyclerView? = null
    private var mListener : FragmentCommutes.OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savecInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.view_shortcuts,
            container,
            false
        )

        return view
    }

    override fun onAttach(context : Context){
        super.onAttach(context)
        if(context is FragmentCommutes.OnFragmentInteractionListener){
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

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(
            fragmentCaller: Int,
            fragmentState: Int
        )
    }
}
