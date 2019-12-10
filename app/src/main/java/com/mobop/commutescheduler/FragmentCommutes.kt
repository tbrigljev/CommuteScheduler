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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_edit.*

/* *************************************************************** */


/* FragmentCommutes ********************************************** */
/* Contains the list of commutes and the related buttons ********* */
/* Contained in FragmentHome and in its standalone fragment ****** */
class FragmentCommutes(screen : Int) : Fragment(){

    var mRecyclerView : RecyclerView? = null
    private var mListener : OnFragmentInteractionListener? = null

    private val fragmentID = 2
    private var source = 0
    private val commutesScreen = screen

    private lateinit var returnCommutesButton : ImageButton
    private lateinit var addCommutesButton : ImageButton
    private lateinit var enhanceCommutesButton : ImageButton
    private lateinit var editCommutesButton : ImageButton

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savecInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.fragment_commutes,
            container,
            false
        )

        enhanceCommutesButton =
            view.findViewById(R.id.commutes_button_enhance)
                    as ImageButton
        addCommutesButton =
            view.findViewById(R.id.commutes_button_add)
                    as ImageButton
        returnCommutesButton =
            view.findViewById(R.id.commutes_button_return)
                    as ImageButton
        /*editCommutesButton =
            view.findViewById(R.id.commutes_button_edit)
                    as ImageButton*/

        enhanceCommutesButton.setOnClickListener{
            doCommutesEnhance(fragmentID)
        }
        addCommutesButton.setOnClickListener{
            doCommutesAdd(fragmentID)
        }
        returnCommutesButton.setOnClickListener{
            doCommutesReturn(fragmentID)
        }
        /*editCommutesButton.setOnClickListener{
            doCommutesEditExisting()
        }*/

        when(commutesScreen){
            0 -> {
                returnCommutesButton.visibility = View.VISIBLE
                addCommutesButton.visibility = View.VISIBLE
                enhanceCommutesButton.visibility = View.GONE
            }
            1 -> {
                returnCommutesButton.visibility = View.GONE
                addCommutesButton.visibility = View.GONE
                enhanceCommutesButton.visibility = View.VISIBLE
            }
        }

        /* Creation of the RecyclerView, LayoutManager and Adapter */
        mRecyclerView = view.findViewById(R.id.commutes_list)
        // mRecyclerView!!.setHasFixedSize(true)

        var mLayoutManager = LinearLayoutManager(context)
        mRecyclerView!!.layoutManager = mLayoutManager

        var mAdapter =
            CommutesAdapter(
                R.layout.element_commute_simple,
                commutesList!!.commutesItemsList,
                { partItem : Int -> listItemClicked(partItem) }
            )

        mRecyclerView!!.adapter = mAdapter

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

    private fun doCommutesEnhance(fragmentCaller : Int){
        if (mListener != null){
            source = 0
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doCommutesReturn(fragmentCaller : Int){
        if (mListener != null){
            source = 1
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doCommutesEditExisting(){
        if (mListener != null){
            val text = "No function implemented"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(context, text, duration)
            toast.show()
        }
    }

    private fun doCommutesAdd(fragmentCaller : Int){
        if (mListener != null){
            source = 2
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : Int)
    }

    private fun listItemClicked(pos : Int){
        if(mListener != null){
            mListener!!.onFragmentInteraction(10, 10)
        }
    }
}