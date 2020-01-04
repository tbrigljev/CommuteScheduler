package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*
/* *************************************************************** */

/* FragmentCommuteEdit ******************************************* */
/* Fragment reserved for the edition of commute elements  ******** */
/* This is used for both edition existing elements and adding ones */

class FragmentCommutesEdit(
    private var new : Boolean,
    private var pos : Int) :
    Fragment(){

    companion object {
        //var start: String = "Fribourg"
        var start : String = ""
        //var arrival: String = "Granges-Paccot"
        var arrival : String = ""
    }
    private var mListener : OnFragmentInteractionListener? = null

    private val fragmentID = 3
    private var source :IntArray = intArrayOf(0, 0)

    private lateinit var cancelEditButton : ImageButton
    private lateinit var validateEditButton : ImageButton

    private lateinit var commuteName : EditText
    private lateinit var commuteOrigin : EditText
    private lateinit var commuteOriginAddress : AutocompleteSupportFragment
    private lateinit var commuteDestination : EditText
    private lateinit var commuteDestinationAddress : AutocompleteSupportFragment

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
            getActivity()!!.getApplicationContext())

        cancelEditButton =
            view.findViewById(R.id.edit_button_cancel)
                    as ImageButton
        validateEditButton =
            view.findViewById(R.id.edit_button_validate)
                    as ImageButton

        commuteName = view.findViewById(R.id.edit_title)

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
                cal.get(Calendar.DAY_OF_MONTH))
                .show()
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
            TimePickerDialog(
                context,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true)
                .show()
        }

        if(new){
            commuteName.setText("")
        } else {
            commuteName.setText(commutesList!!.commutesItemsList[pos].name)
            start = commutesList!!.commutesItemsList[pos].start
            arrival = commutesList!!.commutesItemsList[pos].arrival

            commuteOriginAddress.setText(start)
            commuteDestinationAddress.setText(arrival)
            var date = commutesList!!
                .commutesItemsList[pos]
                .arrival_time_long
                .split(" ")[0]
            var time = commutesList!!
                .commutesItemsList[pos]
                .arrival_time_long
                .split(" ")[1]
            chooseDate.setText(date)
            chooseTime.setText(time)
        }

        return view
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
                    false,
                    false,
                    pos)
            }
        }
    }

    private fun doEditValidate(fragmentCaller : Int){
        if (mListener != null){

            var newCommute = Commute()

            var arrivalDate = chooseDate.text.toString()
            var arrivalTime = chooseTime.text.toString()

            lateinit var text : String

            if(commuteName.text.toString() == "")
                text = "Name of commute is missing"
            else if(start == "")
                text = "Starting location is missing"
            else if(arrival == "")
                text = "Destination is missing"
            else if(arrivalDate == "")
                text = "Date information is missing"
            else if(arrivalTime == "")
                text = "Time information is missing"
            else {
                newCommute.name = commuteName.text.toString()
                newCommute.start = start
                newCommute.arrival = arrival
                newCommute.arrival_time_short =
                    "on " + arrivalDate +
                            ", at " + arrivalTime
                newCommute.arrival_time_long =
                    arrivalDate +
                            " " + arrivalTime

                if(new){

                    text = "Commute added"
                    //var arrival_time = "2019-12-31 23:00:00"
                    commutesList!!.commutesItemsList.add(newCommute)


                    var pos = commutesList!!.commutesItemsList.size - 1
                    MainActivity.mGoogleAPI!!.requestRoute(
                        "Activity", pos, true)

                    var prev_pos = FragmentCommutes.mAdapter!!.previousPosition
                    if(commutesList!!.commutesItemsList.count() < 2){
                        prev_pos = -1
                    }
                    if((prev_pos != -1) and
                        (prev_pos < FragmentCommutes.mAdapter!!.commutesItemsList.size)){
                        FragmentCommutes.mAdapter!!.viewLayouts(
                            false,
                            false,
                            FragmentCommutes.mAdapter!!.previousPosition
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
                        .arrival = newCommute.arrival
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
                        false,
                        false,
                        pos)
                    FragmentCommutes
                        .mRecyclerView!!
                        .adapter!!
                        .notifyDataSetChanged()
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
            }
        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
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