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
import androidx.appcompat.widget.DialogTitle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*


/* *************************************************************** */

/* FragmentEdit ************************************************** */
/* Fragment reserved for the edition of elements  **************** */
/* This is used for both edition existing elements and adding ones */

class FragmentEdit(private var new : Boolean, private var pos : Int) : Fragment(){

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

        commuteName = view.findViewById(R.id.edit_title)
        commuteOrigin = view.findViewById(R.id.edit_start)
        //commuteOriginAddress = .findFragmentById(R.id.fragment_start)
        commuteDestination = view.findViewById(R.id.edit_end)
        //commuteDestinationAddress = .findFragmentById(R.id.fragment_arrival)

        chooseDate =
            view.findViewById(R.id.edit_end_date) as TextView
        chooseTime =
            view.findViewById(R.id.edit_end_time) as TextView

        cancelEditButton.setOnClickListener{
            doEditCancel(fragmentID)
        }

        validateEditButton.setOnClickListener{
            doEditValidate(fragmentID)
        }

        chooseDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, day)
                    chooseDate.text = SimpleDateFormat("DD.MM.YYYY").format(cal.time)
                }
            DatePickerDialog(context, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        chooseTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                chooseTime.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        if(new){
            commuteName.setText("")
            commuteOrigin.setText("")
            commuteDestination.setText("")
        }
        else{
            commuteName.setText(commutesList!!.commutesItemsList[pos].name)
            commuteOrigin.setText(commutesList!!.commutesItemsList[pos].start)
            commuteDestination.setText(commutesList!!.commutesItemsList[pos].arrival)
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
            commuteName.setText("")
            commuteOrigin.setText("")
            commuteDestination.setText("")
            hideKeyboard()
            source[0] = 0
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doEditValidate(fragmentCaller : Int){
        if (mListener != null){

            var newCommute : Commute = Commute()
            var paramComplete = true

            newCommute.name = commuteName.text.toString()
            newCommute.start = commuteOrigin.text.toString()
            newCommute.arrival = commuteDestination.text.toString()

            lateinit var text : String

            if(newCommute.name == ""){
                text = "Name of commute is missing"
                paramComplete = false
            }
            else if(newCommute.start == ""){
                text = "Starting location is missing"
                paramComplete = false
            }
            else if(newCommute.arrival == "") {
                text = "Destination is missing"
                paramComplete = false
            }

            if(paramComplete){
                if(new){
                    text = "Commute added"
                    commutesList!!.commutesItemsList.add(newCommute)
                }
                else{
                    text = "Commute modified"
                    commutesList!!.commutesItemsList[pos] = newCommute
                    FragmentCommutes.mRecyclerView!!.adapter!!.notifyDataSetChanged()


                }
            }

            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(context, text, duration)
            toast.show()

            source[0] = 0
            source[1] = pos
            mListener!!.onFragmentInteraction(fragmentCaller, source)

            commuteName.setText("")
            commuteOrigin.setText("")
            commuteDestination.setText("")
        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : IntArray)
    }
}