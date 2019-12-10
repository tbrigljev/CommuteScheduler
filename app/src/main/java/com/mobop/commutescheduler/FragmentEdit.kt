package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
/* *************************************************************** */

/* FragmentEdit ************************************************** */
/* Fragment reserved for the edition of elements  **************** */
/* This is used for both edition existing elements and adding ones */

class FragmentEdit : Fragment(){

    private var mListener : FragmentEdit.OnFragmentInteractionListener? = null

    private val fragmentID = 3
    private var source = 0

    private lateinit var cancelEditButton : ImageButton
    private lateinit var validateEditButton : ImageButton

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savecInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.fragment_edit,
            container,
            false
        )

        cancelEditButton =
            view.findViewById(R.id.edit_button_cancel) as ImageButton
        validateEditButton =
            view.findViewById(R.id.edit_button_validate) as ImageButton

        cancelEditButton.setOnClickListener{
            doEditCancel(fragmentID)
        }

        validateEditButton.setOnClickListener{
            doEditValidate(fragmentID)
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

    private fun doEditCancel(fragmentCaller : Int){
        if (mListener != null){
            source = 0
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doEditValidate(fragmentCaller : Int){
        if (mListener != null){
            val text = "No function implemented"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(context, text, duration)
            toast.show()
        }
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : Int)
    }
}