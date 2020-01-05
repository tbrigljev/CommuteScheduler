package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
/* *************************************************************** */

/* FragmentCommuteEdit ******************************************* */
/* Fragment reserved for the edition of commute elements  ******** */
/* This is used for both edition existing elements and adding ones */

class FragmentCommutesEdit(
    private var new : Boolean,
    private var pos : Int) :
    Fragment(), AdapterView.OnItemSelectedListener{

    companion object {
        //var start: String = "Fribourg"
        var start_name : String = ""
        var start_address : String = ""
        //var arrival: String = "Granges-Paccot"
        var arrival_name : String = ""
        var arrival_address : String = ""
    }
    private val Origin_SPINNER_ID = 1
    private val Dest_SPINNER_ID = 2

    private var mListener : OnFragmentInteractionListener? = null

    private val fragmentID = 3
    private var source :IntArray = intArrayOf(0, 0)

    private lateinit var cancelEditButton : ImageButton
    private lateinit var validateEditButton : ImageButton

    private lateinit var commuteName : EditText

    private lateinit var commuteOriginFavSpinner : Spinner
    private lateinit var commuteDestinationFavSpinner : Spinner

    private lateinit var commuteOrigin : EditText
    private lateinit var commuteOriginAddress : AutocompleteSupportFragment
    private lateinit var commuteDestination : EditText
    private lateinit var commuteDestinationAddress : AutocompleteSupportFragment
    private lateinit var alarmEnableSwitch : Switch

    private lateinit var chooseDate : TextView
    private lateinit var chooseTime : TextView

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.fragment_commutes_edit,
            container,
            false
        )
        MainActivity.mGoogleAPI!!.setActivityContext(
            this,
            activity!!.applicationContext
        )

        cancelEditButton =
            view.findViewById(R.id.edit_button_cancel)
                    as ImageButton
        validateEditButton =
            view.findViewById(R.id.edit_button_validate)
                    as ImageButton

        commuteName = view.findViewById(R.id.edit_title)

        commuteOriginFavSpinner = view.findViewById(R.id.commuteOriginFavSpinner)
        commuteDestinationFavSpinner = view.findViewById(R.id.commuteDestinationFavSpinner)

        commuteOriginAddress = childFragmentManager
            .findFragmentById(R.id.fragment_start)
                as AutocompleteSupportFragment
        commuteOriginAddress
            .setHint(getString(R.string.field_departure))

        commuteDestinationAddress = childFragmentManager
            .findFragmentById(R.id.fragment_arrival)
                as AutocompleteSupportFragment
        commuteDestinationAddress
            .setHint(getString(R.string.field_arrival))

        chooseDate =
            view.findViewById(R.id.edit_end_date)
                    as TextView
        chooseTime =
            view.findViewById(R.id.edit_end_time)
                    as TextView

        alarmEnableSwitch =
            view.findViewById(R.id.alarmEnableSwitch)
                    as Switch


        cancelEditButton.setOnClickListener{
            doEditCancel(fragmentID)
        }

        validateEditButton.setOnClickListener{
            doEditValidate(fragmentID)
        }

        chooseDate.setOnClickListener{
            val cal = Calendar.getInstance()
            val dateSetListener =
                DatePickerDialog.OnDateSetListener {
                        datePicker,
                        year, month, day ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    chooseDate.text =
                        SimpleDateFormat("YYYY-MM-dd")
                            .format(cal.time)
                }
            DatePickerDialog(
                context,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        chooseTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = OnTimeSetListener {
                    timePicker,
                    hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                chooseTime.text =
                    SimpleDateFormat("HH:mm").
                        format(cal.time) + ":00"
            }
            val timeDialog = TimePickerDialog(
                context,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true)
            timeDialog.show()
        }

        val fav = ArrayList<String>()
        fav.add("")
        commutesList!!.favoritesItemsList.forEach {fav.add(it.name)}

        var aa = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, fav)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(commuteOriginFavSpinner)
        {
            adapter = aa
            setSelection(0, false)
            onItemSelectedListener = this@FragmentCommutesEdit
            prompt = "Select your favourite language"
            gravity = Gravity.CENTER
            id = Origin_SPINNER_ID
        }

        aa = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, fav)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(commuteDestinationFavSpinner)
        {
            adapter = aa
            setSelection(0, false)
            onItemSelectedListener = this@FragmentCommutesEdit
            prompt = "Select your favourite language"
            gravity = Gravity.CENTER
            id = Dest_SPINNER_ID

        }

        if(new){
            commuteName.setText("")
            alarmEnableSwitch.isChecked = true
        } else {
            commuteName.setText(commutesList!!.commutesItemsList[pos].name)
            start_name = commutesList!!.commutesItemsList[pos].start
            start_address = commutesList!!.commutesItemsList[pos].start_address
            arrival_name = commutesList!!.commutesItemsList[pos].arrival
            arrival_address = commutesList!!.commutesItemsList[pos].arrival_address
            alarmEnableSwitch.isChecked = commutesList!!.commutesItemsList[pos].alarm

            var spinPos = fav.indexOf(start_name)

            if (spinPos > -1) commuteOriginFavSpinner.setSelection(spinPos)
            else commuteOriginFavSpinner.setSelection(0)

            spinPos = fav.indexOf(arrival_name)
            if (spinPos > -1) commuteDestinationFavSpinner.setSelection(spinPos)
            else commuteDestinationFavSpinner.setSelection(0)

            //commuteOrigin.setText(start_name)
            commuteOriginAddress.setText(start_address)
            //commuteDestination.setText(arrival_name)
            commuteDestinationAddress.setText(arrival_address)

            val date = commutesList!!
                .commutesItemsList[pos]
                .arrival_time_long
                .split(" ")[0]
            val time = commutesList!!
                .commutesItemsList[pos]
                .arrival_time_long
                .split(" ")[1]
            chooseDate.text = date
            chooseTime.text = time
        }

        return view
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            1 -> {
                if (position > 0){
                    start_name=commutesList!!.favoritesItemsList[position-1].name
                    start_address=commutesList!!.favoritesItemsList[position-1].address
                    commuteOriginAddress.setText(start_address)
                } else {
                    commuteOriginAddress.setText("")
                    start_address=""
                }
            }
            2 -> {
                if (position > 0){
                    arrival_name=commutesList!!.favoritesItemsList[position-1].name
                    arrival_address=commutesList!!.favoritesItemsList[position-1].address
                    commuteDestinationAddress.setText(arrival_address)

                } else {
                    commuteDestinationAddress.setText("")
                    arrival_address=""
                }
            }
        }
    }

    override fun onAttach(context : Context){
        super.onAttach(context)
        if(context is OnFragmentInteractionListener){
            mListener = context
        } else {
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
            commuteName.setText("")

            hideKeyboard()

            source[0] = 0
            mListener!!.onFragmentInteraction(fragmentCaller, source)

            if (pos >= 0){
                FragmentCommutes.mAdapter!!.viewLayouts(
                    visibleLayoutButtons = false,
                    visibleLayoutExtended = false,
                    pos = pos
                )
            }
        }
    }

    private fun doEditValidate(fragmentCaller : Int){
        if (mListener != null){

            val newCommute = Commute()

            //val startName = commuteOrigin.text.toString()
            //val arrivalName = commuteDestination.text.toString()
            val arrivalDate = chooseDate.text.toString()
            val arrivalTime = chooseTime.text.toString()

            val format = "yyyy-MM-dd HH:mm:ss"
            val sdf = SimpleDateFormat(format)

            lateinit var text : String

            if(commuteName.text.toString() == "")
                text = "Name of commute is missing"
            else if(start_address == "")
                text = "Starting location is missing"
            else if(arrival_address == "")
                text = "Destination is missing"
            else if(arrivalDate == "")
                text = "Date information is missing"
            else if(arrivalTime == "")
                text = "Time information is missing"
            else{
                val arrivalDateTime = arrivalDate + " " + arrivalTime
                val timeArrival = sdf.parse(arrivalDateTime)
                val timeNow = Calendar.getInstance().time

                if(timeArrival <= timeNow){
                    text = "Please chose a later date and/or time"
                } else {
                    newCommute.name = commuteName.text.toString()
                    newCommute.start = start_name
                    newCommute.start_address = start_address
                    newCommute.arrival = arrival_name
                    newCommute.arrival_address = arrival_address

                    val time = arrivalTime.split(":")
                    val date = arrivalDate.split("-")
                    newCommute.arrival_time_short =
                        "on " + date[2] + "." + date[1] + "." + date[0] +
                                ", at " + time[0] + ":" + time[1]
                    newCommute.arrival_time_long =
                        arrivalDate +
                                " " + arrivalTime

                    newCommute.alarm = alarmEnableSwitch.isChecked
                    if(new){

                        text = "Commute added"
                        //var arrival_time = "2019-12-31 23:00:00"
                        commutesList!!.commutesItemsList.add(newCommute)


                        val pos = commutesList!!.commutesItemsList.size - 1
                        MainActivity.mGoogleAPI!!.requestRoute(
                            "Activity", pos, true)

                        var prevPos = FragmentCommutes.mAdapter!!.previousPosition
                        if(commutesList!!.commutesItemsList.count() < 2){
                            prevPos = -1
                        }
                        if((prevPos != -1) and
                            (prevPos < FragmentCommutes.mAdapter!!.commutesItemsList.size)){
                            FragmentCommutes.mAdapter!!.viewLayouts(
                                visibleLayoutButtons = false,
                                visibleLayoutExtended = false,
                                pos = FragmentCommutes.mAdapter!!.previousPosition
                            )
                        }
                    } else {
                        text = "Commute modified"
                        commutesList!!
                            .commutesItemsList[pos]
                            .name = newCommute.name
                        commutesList!!
                            .commutesItemsList[pos]
                            .start = newCommute.start
                        commutesList!!
                            .commutesItemsList[pos]
                            .start_address = newCommute.start_address
                        commutesList!!
                            .commutesItemsList[pos]
                            .arrival = newCommute.arrival
                        commutesList!!
                            .commutesItemsList[pos]
                            .arrival_address = newCommute.arrival_address
                        commutesList!!
                            .commutesItemsList[pos]
                            .arrival_time_long = newCommute.arrival_time_long
                        commutesList!!
                            .commutesItemsList[pos]
                            .arrival_time_short = newCommute.arrival_time_short

                        MainActivity.mGoogleAPI!!.requestRoute(
                            "Activity",
                            pos,
                            false)

                        FragmentCommutes.mAdapter!!.viewLayouts(
                            visibleLayoutButtons = false,
                            visibleLayoutExtended = false,
                            pos = pos
                        )
                        FragmentCommutes
                            .mRecyclerView!!
                            .adapter!!
                            .notifyDataSetChanged()
                    }
                }
            }

            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(context, text, duration)
            toast.show()

            if((text == "Commute added") or
                (text == "Commute modified")){
                source[0] = 0
                source[1] = pos
                mListener!!.onFragmentInteraction(fragmentCaller, source)
                commuteName.setText("")
                alarmEnableSwitch.isChecked = true
                //commuteOrigin.setText("")
                //commuteDestination.setText("")
            }
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE)
                    as InputMethodManager
        inputMethodManager
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : IntArray)
    }
}