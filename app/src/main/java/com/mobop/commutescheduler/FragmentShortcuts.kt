package com.mobop.commutescheduler

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class FragmentShortcuts : Fragment(){

    private var mListener : FragmentEdit.OnFragmentInteractionListener? = null

    private val fragmentID = 0
    private var source : IntArray = intArrayOf(0, 0)

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

        var favoritesButton : ImageButton = view.findViewById(R.id.shortcut_button_favorites)
        var quickButton : ImageButton = view.findViewById(R.id.shortcut_button_quick)
        var addButton : ImageButton = view.findViewById(R.id.shortcut_button_add)

        favoritesButton.setOnClickListener{
            doOpenFavorites(fragmentID)
        }

        quickButton.setOnClickListener{
            doOpenQuick(fragmentID)
        }

        addButton.setOnClickListener{
            doOpenAddNew(fragmentID)
        }

        return view
    }

    override fun onAttach(context : Context){
        super.onAttach(context)
        if(context is FragmentEdit.OnFragmentInteractionListener){
            mListener = context
        } else{
            throw RuntimeException(context.toString() +
                    " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach(){
        super.onDetach()
        mListener = null
    }

    private fun doOpenFavorites(fragmentCaller: Int){
        if (mListener != null){
            source[0] = 1
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doOpenQuick(fragmentCaller: Int){
        if (mListener != null){
            source[0] = 2
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doOpenAddNew(fragmentCaller : Int){
        if (mListener != null){
            source[0] = 3
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : IntArray)
    }
}
