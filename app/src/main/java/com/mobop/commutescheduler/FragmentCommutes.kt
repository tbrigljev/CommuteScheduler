package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
/* *************************************************************** */

/* FragmentCommutes ********************************************** */
/* Contains the list of commutes and the related buttons ********* */
/* Contained in FragmentHome and in its standalone fragment ****** */
class FragmentCommutes(screen : Int) : Fragment(){
    companion object{
        var mRecyclerView : RecyclerView? = null
        var mAdapter : CommutesAdapter? = null
    }

    private var mListener : OnFragmentInteractionListener? = null

    private val fragmentID = 2
    private var source : IntArray = intArrayOf(0,0)
    private val commutesScreen = screen
    private var empty : Boolean = true

    private lateinit var returnCommutesButton : ImageButton
    private lateinit var addCommutesButton : ImageButton
    private lateinit var enhanceCommutesButton : ImageButton
    private lateinit var emptyCommutes : ConstraintLayout
    private lateinit var emptyCommutesAdd : ImageButton

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.fragment_commutes,
            container,
            false
        )

        emptyCommutes =
            view.findViewById(R.id.commutes_empty_container)
                    as ConstraintLayout
        emptyCommutesAdd =
            view.findViewById(R.id.commutes_button_empty)
                    as ImageButton

        enhanceCommutesButton =
            view.findViewById(R.id.commutes_button_enhance)
                    as ImageButton
        addCommutesButton =
            view.findViewById(R.id.commutes_button_add)
                    as ImageButton
        returnCommutesButton =
            view.findViewById(R.id.commutes_button_return)
                    as ImageButton

        if(commutesList!!.commutesItemsList.count() < 1){
            empty = true
            addCommutesButton.visibility = View.GONE
        } else {
            empty = false
            emptyCommutes.visibility = View.GONE
        }

        enhanceCommutesButton.setOnClickListener{
            doCommutesEnhance(fragmentID)
        }
        addCommutesButton.setOnClickListener{
            doCommutesAdd(fragmentID, empty)
        }
        emptyCommutesAdd.setOnClickListener {
            doCommutesAdd(fragmentID, empty)
        }
        returnCommutesButton.setOnClickListener{
            doCommutesReturn(fragmentID)
        }

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

        val mLayoutManager = LinearLayoutManager(context)
        mRecyclerView!!.layoutManager = mLayoutManager

        mAdapter =
            CommutesAdapter(mRecyclerView!!,
                R.layout.element_commute_combined,
                commutesList!!.commutesItemsList,
                { partItem : Int,
                  action : Int ->
                    doLayoutButtons(partItem, action) }
            )

        mRecyclerView!!.adapter = mAdapter

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

    private fun doCommutesEnhance(fragmentCaller : Int){
        if (mListener != null){
            source[0] = 0

            if(emptyCommutes.visibility == View.VISIBLE){
                addCommutesButton.visibility = View.GONE
            } else {
                addCommutesButton.visibility = View.VISIBLE
            }
            returnCommutesButton.visibility = View.VISIBLE
            enhanceCommutesButton.visibility = View.GONE

            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doCommutesReturn(fragmentCaller : Int){
        if (mListener != null){
            source[0] = 1
            mListener!!
                .onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doCommutesAdd(
        fragmentCaller : Int,
        empty : Boolean){
        if (mListener != null){
            source[0] = 2
            mListener!!
                .onFragmentInteraction(fragmentCaller, source)

            if(commutesList!!.commutesItemsList.count() > 0){
                emptyCommutes.visibility = View.GONE
                addCommutesButton.visibility = View.VISIBLE
            }
        }
    }

    private fun doLayoutButtons(partItem : Int, action : Int){
        when(action){
            3 -> {
                 if(mListener != null){
                     //layoutButtons.visibility = View.GONE
                     source = intArrayOf(action, partItem)
                     mListener!!
                         .onFragmentInteraction(fragmentID, source)
                 }
            }
            4 -> {
                if(
                    commutesList!!.commutesItemsList[partItem].name ==
                    FragmentMap.mapFieldCommuteName.text.toString()){
                    FragmentMap.mMap.clear()
                    FragmentMap.mapFieldCommuteNText.visibility = View.VISIBLE
                    FragmentMap.mapFieldCommuteNText.text = getString(R.string.no_commute)
                    FragmentMap.mapFieldCommuteName.visibility = View.GONE
                    FragmentMap.mapFieldCommuteDText.visibility = View.GONE
                    FragmentMap.mapFieldCommuteDuration.visibility = View.GONE
                }

                mAdapter!!.viewLayouts(
                    visibleLayoutButtons = false,
                    visibleLayoutExtended = false,
                    pos = partItem
                )
                mAdapter!!.removeAt(partItem)

                mRecyclerView!!.adapter!!.notifyDataSetChanged()

                if(commutesList!!.commutesItemsList.count() < 1){
                    emptyCommutes.visibility = View.VISIBLE
                    addCommutesButton.visibility = View.GONE
                }

                val text = "Commute deleted"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(
                    activity,
                    text,
                    duration)
                toast.show()
            }
        }
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : IntArray)
    }
}