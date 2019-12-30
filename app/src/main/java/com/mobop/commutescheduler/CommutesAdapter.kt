package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.media.Image
import android.text.TextUtils.indexOf
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.find
import org.w3c.dom.Text
import androidx.core.view.ViewCompat.setY
import androidx.recyclerview.widget.ItemTouchHelper

/* *************************************************************** */

/* CommutesAdapter *********************************************** */
/* Adapter for the recycler view managing the list of commutes *** */
/* Contained in FragmentCommutes ********************************* */
class CommutesAdapter(viewRes : Int, commutesItemsList: ArrayList<Commute>,
                      private val touchListener : (Int, Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                      //private val touchListener : (Commute, Int) -> Unit) :
    private lateinit var view : View

    var commutesItemsList : ArrayList<Commute>
    private var viewRes : Int = 0

    init{
        this.commutesItemsList = commutesItemsList
        this.viewRes = viewRes
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder{

        view = LayoutInflater.from(parent.context).inflate(
                R.layout.element_commute_combined,
                parent,
                false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder : RecyclerView.ViewHolder,
        position : Int){

        /*
        val editButton =
            view.findViewById(R.id.commutes_button_edit) as ImageButton
        val deleteButton =
            view.findViewById(R.id.commutes_button_delete) as ImageButton*/

        //var commute : Commute = commutesItemsList[position]
        //editButton.setOnClickListener{ touchListener(commute, position) }
        //deleteButton.setOnClickListener{ touchListener(commute, position) }
        //editButton.setOnClickListener{ touchListener(position) }
        //deleteButton.setOnClickListener{ touchListener(position) }

        val viewHolder = holder as ViewHolder
        viewHolder.bind(
            view,
            this.commutesItemsList,
            position,
            touchListener)

        holder.editCommute(commutesItemsList.indexOf(commutesItemsList[position]), 3)
        holder.deleteCommute(commutesItemsList.indexOf(commutesItemsList[position]), 4)
    }

    override fun getItemCount() : Int{
        return commutesItemsList.count()
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val title : TextView

        val simpleStart : TextView
        val simpleEnd : TextView
        val simpleTimeDuration : TextView

        val extendedStart : TextView
        val extendedStartAddress : TextView
        val extendedEnd : TextView
        val extendedEndAddress : TextView
        val extendedTimeStart : TextView
        val extendedTimeEnd : TextView
        val extendedTimeDuration : TextView
/*        val extendedReminderOn : Switch
        val extendedReminder : TextView
        val extendedAlarmOn : Switch
        val extendedAlarmTime : TextView
        val extendedAlarm : TextView*/

        val layoutCombined : ConstraintLayout =
            view.findViewById(R.id.element_combined) as ConstraintLayout
        val layoutSimple : LinearLayout =
            view.findViewById(R.id.element_simple_container) as LinearLayout
        val layoutExtended : LinearLayout =
            view.findViewById(R.id.element_extended_container) as LinearLayout
        val layoutButtons : ConstraintLayout =
            view.findViewById(R.id.buttons_container) as ConstraintLayout

        val editButton =
            view.findViewById(R.id.commutes_button_edit) as ImageButton
        val deleteButton =
            view.findViewById(R.id.commutes_button_delete) as ImageButton

        fun editCommute(pos : Int, action : Int){
            editButton.setOnClickListener{ touchListener(pos, action) }
        }
        fun deleteCommute(pos : Int, action : Int){
            deleteButton.setOnClickListener{ touchListener(pos, action) }
        }

        init{
            title = view.findViewById(R.id.element_combined_title) as TextView

            simpleStart = view.findViewById(R.id.element_simple_start) as TextView
            simpleEnd = view.findViewById(R.id.element_simple_end) as TextView
            simpleTimeDuration = view.findViewById(R.id.element_simple_time) as TextView

            extendedStart = view.findViewById(R.id.element_extended_start) as TextView
            extendedStartAddress = view.findViewById(R.id.element_extended_start_address) as TextView
            extendedEnd = view.findViewById(R.id.element_extended_end) as TextView
            extendedEndAddress = view.findViewById(R.id.element_extended_end_address) as TextView
            extendedTimeStart = view.findViewById(R.id.element_extended_start_time) as TextView
            extendedTimeEnd = view.findViewById(R.id.element_extended_end_time) as TextView
            extendedTimeDuration = view.findViewById(R.id.element_extended_duration) as TextView
/*            extendedReminderOn = view.findViewById(R.id.sw_reminder) as Switch
            extendedReminder = view.findViewById(R.id.reminder_tune) as TextView
            extendedAlarmOn = view.findViewById(R.id.sw_alarm) as Switch
            extendedAlarmTime = view.findViewById(R.id.alarm_time) as TextView
            extendedAlarm = view.findViewById(R.id.alarm_tune) as TextView*/

            layoutExtended.visibility = View.GONE
            layoutButtons.visibility = View.GONE
        }

        fun bind(
            view : View,
            commutesItemsList : ArrayList<Commute>,
            position : Int,
            touchListener : (Int, Int) -> Unit){
            val itemInList = commutesItemsList[position]

            if (position % 2 == 0){
                layoutCombined.setBackgroundColor(
                    ContextCompat.getColor(view.context, R.color.colorCommutesEven))
            } else{
                layoutCombined.setBackgroundColor(
                    ContextCompat.getColor(view.context, R.color.colorCommutesOdd))
            }

            if (itemInList != null) {
                val elementTitle = itemInList.name
                title.text = elementTitle

                val elementSimpleStart = itemInList.start
                simpleStart.text = elementSimpleStart
                val elementSimpleEnd = itemInList.arrival
                simpleEnd.text = elementSimpleEnd
                val elementSimpleTime = itemInList.start_time
                simpleTimeDuration.text = elementSimpleTime

                val elementExtendedStart = itemInList.start
                extendedStart.text = elementExtendedStart
                val elementExtendedStartAddress = itemInList.start_address
                extendedStartAddress.text = elementExtendedStartAddress
                val elementExtendedEnd = itemInList.arrival
                extendedEnd.text = elementExtendedEnd
                val elementExtendedEndAddress = itemInList.arrival_address
                extendedEndAddress.text = elementExtendedEndAddress
                val elementTimeDeparture = itemInList.start_time
                extendedTimeStart.text = elementTimeDeparture
                val elementTimeArrival = itemInList.arrival_time
                extendedTimeEnd.text = elementTimeArrival
                val elementTimeDuration = itemInList.duration
                extendedTimeDuration.text = elementTimeDuration
                /*val elementReminderOn = itemInList.reminder_on
                extendedReminderOn.isChecked = elementReminderOn
                val elementReminder = itemInList.reminder_tune
                extendedReminder.text = elementReminder
                val elementAlarmOn = itemInList.alarm_on
                extendedAlarmOn.isChecked = elementAlarmOn
                val elementAlarmTime = itemInList.alarm_time
                extendedAlarmTime.text = elementAlarmTime
                val elementAlarm = itemInList.alarm_tune
                extendedAlarm.text = elementAlarm*/
            }

            itemView.setOnTouchListener(object : OnSwipeTouchListener() {
                override fun onClick() {
                    //touchListener(position)
                    if(layoutButtons.visibility == View.VISIBLE) {
                        layoutButtons.visibility = View.GONE
                    } else {
                        when(layoutExtended.visibility){
                            View.GONE -> {
                                layoutExtended.visibility = View.VISIBLE
                                layoutSimple.visibility = View.GONE
                            }
                            View.VISIBLE -> {
                                layoutExtended.visibility = View.GONE
                                layoutSimple.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                override fun onSwipeRight() {
                    layoutButtons.visibility = View.VISIBLE
                }
                override fun onSwipeLeft() {
                    layoutButtons.visibility = View.VISIBLE
                }
            })
        }
    }
}