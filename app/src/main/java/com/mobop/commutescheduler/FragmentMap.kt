package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
/* *************************************************************** */

/* FragmentMap *************************************************** */
/* Contains the map element and the related buttons ************** */
/* Contained in FragmentHome and in its standalone fragment ****** */
class FragmentMap(screen : Int) : Fragment(){

    private var mListener : OnFragmentInteractionListener? = null

    private val mapScreen = screen

    private var fragmentID = 1
    private var source = 0

    private lateinit var returnMapButton : ImageButton
    private lateinit var addMapButton : ImageButton
    private lateinit var enhanceMapButton : ImageButton

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savecInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.fragment_map,
            container,
            false
        )

        returnMapButton =
            view.findViewById(R.id.map_button_return) as ImageButton
        addMapButton =
            view.findViewById(R.id.map_button_add) as ImageButton
        enhanceMapButton =
            view.findViewById(R.id.map_button_enhance) as ImageButton

        enhanceMapButton.setOnClickListener{
            doMapEnhance(fragmentID)
        }

        returnMapButton.setOnClickListener{
            doMapReturn(fragmentID)
        }

        when(mapScreen){
            0 -> {
                returnMapButton.visibility = View.VISIBLE
                addMapButton.visibility = View.VISIBLE
                enhanceMapButton.visibility = View.GONE
            }
            1 -> {
                returnMapButton.visibility = View.GONE
                addMapButton.visibility = View.GONE
                enhanceMapButton.visibility = View.VISIBLE
            }
        }

        return view
    }

    override fun onAttach(context : Context){
        super.onAttach(context)
        if(context is OnFragmentInteractionListener){
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

    private fun doMapEnhance(fragmentCaller : Int){
        if (mListener != null){
            source = 0
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doMapReturn(fragmentCaller : Int){
        if (mListener != null){
            source = 1
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : Int)
    }
}
