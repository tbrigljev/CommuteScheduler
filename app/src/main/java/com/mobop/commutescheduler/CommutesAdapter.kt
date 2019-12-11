package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.find
import org.w3c.dom.Text

/* *************************************************************** */

/* CommutesAdapter *********************************************** */
/* Adapter for the recycler view managing the list of commutes *** */
/* Contained in FragmentCommutes ********************************* */
class CommutesAdapter(
    viewRes : Int,
    commutesItemsList: ArrayList<Commute>,
    val clickListener : (Int) -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var commutesItemsList : ArrayList<Commute>
    private var viewRes : Int = 0

    init{
        this.commutesItemsList = commutesItemsList
        this.viewRes = viewRes
    }

    override fun onCreateViewHolder(
        parent : ViewGroup,
        viewType : Int) :
            CommutesAdapter.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(
                R.layout.element_commute_combined,
                parent,
                false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder : RecyclerView.ViewHolder,
        position : Int){
        val ViewHolder = holder as ViewHolder
        ViewHolder.bind(
            this.commutesItemsList,
            position,
            clickListener)
    }

    override fun getItemCount() : Int{
        return commutesItemsList.count()
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
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
        val extendedReminderOn : Switch
        val extendedReminder : TextView
        val extendedAlarmOn : Switch
        val extendedAlarmTime : TextView
        val extendedAlarm : TextView

        val layoutCombined : ConstraintLayout =
            view.findViewById(R.id.element_combined) as ConstraintLayout
        val layoutSimple : LinearLayout =
            view.findViewById(R.id.element_simple_container) as LinearLayout
        val layoutExtended : LinearLayout =
            view.findViewById(R.id.element_extended_container) as LinearLayout
        val layoutButtons : ConstraintLayout =
            view.findViewById(R.id.buttons_container) as ConstraintLayout

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
            extendedReminderOn = view.findViewById(R.id.sw_reminder) as Switch
            extendedReminder = view.findViewById(R.id.reminder_tune) as TextView
            extendedAlarmOn = view.findViewById(R.id.sw_alarm) as Switch
            extendedAlarmTime = view.findViewById(R.id.alarm_time) as TextView
            extendedAlarm = view.findViewById(R.id.alarm_tune) as TextView

            layoutExtended.visibility = View.GONE
            layoutButtons.visibility = View.GONE
        }

        fun bind(
            commutesItemsList : ArrayList<Commute>,
            position : Int,
            clickListener : (Int) -> Unit){
            val itemInList = commutesItemsList[position]

            if (position % 2 == 0){
                layoutCombined.setBackgroundColor(Color.parseColor("#CCFFCC"))
            } else{
                layoutCombined.setBackgroundColor(Color.parseColor("#CCFFFF"))
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
                val elementReminderOn = itemInList.reminder_on
                extendedReminderOn.isChecked = elementReminderOn
                val elementReminder = itemInList.reminder_tune
                extendedReminder.text = elementReminder
                val elementAlarmOn = itemInList.alarm_on
                extendedAlarmOn.isChecked = elementAlarmOn
                val elementAlarmTime = itemInList.alarm_time
                extendedAlarmTime.text = elementAlarmTime
                val elementAlarm = itemInList.alarm_tune
                extendedAlarm.text = elementAlarm
            }


            itemView.setOnClickListener{
                clickListener(position)
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

            /*
            itemView.setOnTouchListener(object : OnSwipeTouchListener() {
                override fun onClick() {
                    clickListener(position)
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
            })

            itemView.setOnTouchListener(object : OnSwipeTouchListener() {
                override fun onSwipeRight() {
                    layoutButtons.visibility = View.VISIBLE
                }
            })*/
        }
    }
}