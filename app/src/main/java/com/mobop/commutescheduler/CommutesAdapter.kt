package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
/* *************************************************************** */

/* CommutesAdapter *********************************************** */
/* Adapter for the recycler view managing the list of commutes *** */
/* Contained in FragmentCommutes ********************************* */

class CommutesAdapter(
    mRecyclerView: RecyclerView,
    viewRes : Int,
    commutesItemsList: ArrayList<Commute>,
    private val touchListener : (Int, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var view : View

    var commutesItemsList : ArrayList<Commute>
    var previousPosition: Int = 0

    private var viewRes : Int = 0
    private var mRecyclerView: RecyclerView? = null

    init{
        this.commutesItemsList = commutesItemsList
        this.viewRes = viewRes
        this.mRecyclerView = mRecyclerView
    }

    override fun onCreateViewHolder(
        parent : ViewGroup, viewType : Int) :
            ViewHolder{

        view = LayoutInflater.from(parent.context).inflate(
                R.layout.element_commute_combined,
                parent,
                false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder : RecyclerView.ViewHolder,
        position : Int){

        val viewHolder = holder as ViewHolder
        viewHolder.bind(
            view,
            this.commutesItemsList,
            position,
            touchListener)

        holder.editCommute(
            commutesItemsList.indexOf(
                commutesItemsList[position]),
            3)
        holder.deleteCommute(
            commutesItemsList.indexOf(
                commutesItemsList[position]),
            4)
    }

    override fun getItemCount() : Int{
        return commutesItemsList.count()
    }

    inner class ViewHolder(view : View) :
        RecyclerView.ViewHolder(view){

        private val title : TextView

        private val simpleStart : TextView
        private val simpleEnd : TextView
        private val simpleTimeDuration : TextView

        private val extendedStart : TextView
        private val extendedEnd : TextView
        private val extendedTimeStart : TextView
        private val extendedTimeEnd : TextView
        private val extendedTimeDuration : TextView

        private val layoutCombined : ConstraintLayout =
            view.findViewById(R.id.element_combined)
                    as ConstraintLayout
        val layoutSimple : LinearLayout =
            view.findViewById(R.id.element_simple_container)
                    as LinearLayout
        val layoutExtended : LinearLayout =
            view.findViewById(R.id.element_extended_container)
                    as LinearLayout
        val layoutButtons : ConstraintLayout =
            view.findViewById(R.id.buttons_container)
                    as ConstraintLayout

        private val editButton =
            view.findViewById(R.id.commutes_button_edit)
                    as ImageButton
        private val deleteButton =
            view.findViewById(R.id.commutes_button_delete)
                    as ImageButton

        fun editCommute(pos : Int, action : Int){
            editButton.setOnClickListener{
                touchListener(pos, action) }
        }
        fun deleteCommute(pos : Int, action : Int){
            deleteButton.setOnClickListener{
                touchListener(pos, action) }
        }

        init{
            title =
                view.findViewById(R.id.element_combined_title)
                        as TextView

            simpleStart =
                view.findViewById(R.id.element_simple_start)
                        as TextView
            simpleEnd =
                view.findViewById(R.id.element_simple_end)
                        as TextView
            simpleTimeDuration =
                view.findViewById(R.id.element_simple_time)
                        as TextView

            extendedStart =
                view.findViewById(R.id.element_extended_start)
                        as TextView
            extendedEnd =
                view.findViewById(R.id.element_extended_end)
                        as TextView
            extendedTimeStart =
                view.findViewById(R.id.element_extended_start_time)
                        as TextView
            extendedTimeEnd =
                view.findViewById(R.id.element_extended_end_time)
                        as TextView
            extendedTimeDuration =
                view.findViewById(R.id.element_extended_duration)
                        as TextView

            layoutExtended.visibility = View.GONE
            layoutButtons.visibility = View.GONE
        }

        fun bind(
            view : View,
            commutesItemsList : ArrayList<Commute>,
            position : Int,
            touchListener : (Int, Int) -> Unit){
            val itemInList = commutesItemsList[position]

            if(position % 2 == 0){
                layoutCombined.setBackgroundColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.colorCommutesEven))
            } else{
                layoutCombined.setBackgroundColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.colorCommutesOdd))
            }

            if((itemInList != null) and
                (itemInList.duration_val!= null)){

                val elementTitle = itemInList.name
                title.text = elementTitle

                if(itemInList.start == ""){
                    val elementSimpleStart =
                        itemInList.start_address
                    simpleStart.text =
                        elementSimpleStart
                } else {
                    val elementSimpleStart =
                        itemInList.start
                    simpleStart.text =
                        elementSimpleStart
                }
                if(itemInList.arrival == ""){
                    val elementSimpleEnd =
                        itemInList.arrival_address
                    simpleEnd.text =
                        elementSimpleEnd
                } else {
                    val elementSimpleEnd =
                        itemInList.arrival
                    simpleEnd.text =
                        elementSimpleEnd
                }

                val elementExtendedStart =
                    itemInList.start_address
                extendedStart.text =
                    elementExtendedStart
                val elementExtendedEnd =
                    itemInList.arrival_address
                extendedEnd.text =
                    elementExtendedEnd
                var elementSimpleTime = 0
                if (itemInList.duration_traffic_val != null){
                    elementSimpleTime =
                        itemInList.duration_traffic_val!!.toInt()/60
                } else {
                    //elementSimpleTime = itemInList.duration_val!!.toInt()/60
                }

                var hoursText = ""
                var minutesText = ""
                if(elementSimpleTime >= 60){
                    hoursText =
                        (elementSimpleTime/60).toString() + "h"
                    val minutes = elementSimpleTime%60
                    if(minutes != 0){
                        minutesText =
                            (elementSimpleTime%60).toString() + "min"
                    }
                } else {
                    minutesText = elementSimpleTime.toString() +
                            "min"
                }
                simpleTimeDuration.text = hoursText + minutesText

                val elementTimeDeparture =
                    itemInList.start_time_short
                extendedTimeStart.text =
                    elementTimeDeparture
                val elementTimeArrival =
                    itemInList.arrival_time_short
                extendedTimeEnd.text =
                    elementTimeArrival
                val elementTimeDuration =
                    itemInList.duration
                extendedTimeDuration.text =
                    elementTimeDuration
            }

            itemView.setOnTouchListener(
                object : OnSwipeTouchListener(){
                override fun onClick(){
                    if(previousPosition != adapterPosition){
                        try {
                            val viewPrevious : View =
                                mRecyclerView!!
                                    .findViewHolderForAdapterPosition(
                                    previousPosition
                                )!!.itemView
                            val layoutButtonsPrevious : ConstraintLayout =
                                viewPrevious
                                    .findViewById(R.id.buttons_container)
                                        as ConstraintLayout
                            val layoutExtendedPrevious : LinearLayout =
                                viewPrevious
                                    .findViewById(R.id.element_extended_container)
                                        as LinearLayout
                            val layoutSimplePrevious : LinearLayout =
                                viewPrevious
                                    .findViewById(R.id.element_simple_container)
                                        as LinearLayout

                            layoutButtonsPrevious.visibility = View.GONE
                            layoutExtendedPrevious.visibility = View.GONE
                            layoutSimplePrevious.visibility = View.VISIBLE

                            previousPosition = adapterPosition

                        } catch(e : Exception) {
                            Log.i("CommutesClickException", e.toString())
                        }
                    }

                    if(layoutButtons.visibility == View.VISIBLE){
                        layoutButtons.visibility = View.GONE
                    } else {
                        when(layoutExtended.visibility){
                            View.GONE -> {
                                layoutExtended.visibility = View.VISIBLE
                                layoutSimple.visibility = View.GONE
                                val pos = adapterPosition
                                var commute : Commute =
                                    commutesList!!.commutesItemsList[pos]

                                MainActivity.mGoogleAPI!!.requestRoute(
                                    "Activity",
                                    pos,
                                    false)
                            }
                            View.VISIBLE -> {
                                layoutExtended.visibility = View.GONE
                                layoutSimple.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                override fun onSwipeRight(){
                    if (previousPosition != adapterPosition) {
                        val viewPrevious : View =
                            mRecyclerView!!
                                .findViewHolderForAdapterPosition(previousPosition)!!
                                .itemView
                        val layoutButtonsPrevious : ConstraintLayout =
                            viewPrevious
                                .findViewById(R.id.buttons_container)
                                    as ConstraintLayout
                        val layoutExtendedPrevious : LinearLayout =
                            viewPrevious
                                .findViewById(R.id.element_extended_container)
                                    as LinearLayout
                        val layoutSimplePrevious : LinearLayout =
                            viewPrevious
                                .findViewById(R.id.element_simple_container)
                                    as LinearLayout

                        layoutButtonsPrevious.visibility = View.GONE
                        layoutExtendedPrevious.visibility = View.GONE
                        layoutSimplePrevious.visibility = View.VISIBLE

                        previousPosition = adapterPosition
                    }
                    layoutButtons.visibility = View.VISIBLE
                }

                override fun onSwipeLeft(){
                    if (previousPosition != adapterPosition) {
                        val viewPrevious : View =
                            mRecyclerView!!
                                .findViewHolderForAdapterPosition(previousPosition)!!
                                .itemView
                        val layoutButtonsPrevious : ConstraintLayout =
                            viewPrevious
                                .findViewById(R.id.buttons_container)
                                    as ConstraintLayout
                        val layoutExtendedPrevious : LinearLayout =
                            viewPrevious
                                .findViewById(R.id.element_extended_container)
                                    as LinearLayout
                        val layoutSimplePrevious : LinearLayout =
                            viewPrevious
                                .findViewById(R.id.element_simple_container)
                                    as LinearLayout

                        layoutButtonsPrevious.visibility = View.GONE
                        layoutExtendedPrevious.visibility = View.GONE
                        layoutSimplePrevious.visibility = View.VISIBLE

                        previousPosition = adapterPosition
                    }
                    layoutButtons.visibility = View.VISIBLE
                }
            })
        }
    }

    fun removeAt(position: Int){
        doAsync{
            commutesList!!
                .database
                .deleteAll(commutesItemsList[position])

        }
        commutesItemsList.removeAt(position)
        previousPosition = 0
        notifyItemRemoved(position)
        }

    fun viewLayouts(
       visibleLayoutButtons : Boolean,
       visibleLayoutExtended : Boolean,
       pos : Int){
       val viewPos : View =
           mRecyclerView!!
               .findViewHolderForAdapterPosition(pos)!!
               .itemView

       val layoutButtonsPos : ConstraintLayout =
           viewPos
               .findViewById(R.id.buttons_container)
                   as ConstraintLayout
       val layoutExtendedPos : LinearLayout =
           viewPos
               .findViewById(R.id.element_extended_container)
                   as LinearLayout
       val layoutSimplePos : LinearLayout =
           viewPos
               .findViewById(R.id.element_simple_container)
                   as LinearLayout

       if (visibleLayoutButtons){
           layoutButtonsPos.visibility = View.VISIBLE
       } else {
           layoutButtonsPos.visibility = View.GONE
       }

       if (visibleLayoutExtended){
           layoutExtendedPos.visibility = View.VISIBLE
           layoutSimplePos.visibility = View.GONE
       } else {
           layoutExtendedPos.visibility = View.GONE
           layoutSimplePos.visibility = View.VISIBLE
       }
    }
}