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

/* FragmentSettings ********************************************** */
/* Contains the "About" tab for the application ****************** */

class FragmentSettings : Fragment(){

    private var mListener : OnFragmentInteractionListener? = null

    private val fragmentID = 99
    private var source : IntArray = intArrayOf(0,0)

    private lateinit var quickStartButton : ImageButton
    private lateinit var returnSettingsButton : ImageButton

    //C2D
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

        quickStartButton =
            view.findViewById(R.id.settings_start_guide_button)
                    as ImageButton
        quickStartButton.setOnClickListener {
            doSettingsQuickStart(fragmentID)
        }

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
            throw RuntimeException(context.toString() +
                    " must implement OnFragmentInteractionListener")
        }
    }
    override fun onDetach(){
        super.onDetach()
        mListener = null
    }

    private fun doSettingsQuickStart(fragmentCaller : Int){
        if (mListener != null){
            source[0] = 1
            mListener!!
                .onFragmentInteraction(fragmentCaller, source)
        }
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