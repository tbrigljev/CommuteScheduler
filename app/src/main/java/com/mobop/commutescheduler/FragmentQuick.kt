package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.graphics.*
import android.view.*
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_quick.*
import androidx.core.view.children
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
/* *************************************************************** */

/* FragmentQuick ************************************************* */
/* Contains the quick route selection tools ********************** */
/* Contained in its standalone fragment ************************** */

class FragmentQuick : Fragment(){

    private var mListener :
            FragmentCommutesEdit
            .OnFragmentInteractionListener? = null

    private var source : IntArray = intArrayOf(0, 0)

    private val fragmentID = 6
    private var startIcon : String? = null
    private var endIcon : String? = null
    private var startIconAddress : String? = null
    private var endIconAddress : String? = null
    private var commuteName : String? = null
    private var chooseDate : String? = null
    private var chooseTime : String? = null
    private var mPaint : Paint? = null
    private var mCanvas : FingerLine? = null

    companion object{
        var startX : Float = 0.toFloat()
        var startY : Float = 0.toFloat()
        var endX : Float = 0.toFloat()
        var endY : Float = 0.toFloat()

        var startXIcon : Float = 0.toFloat()
        var startYIcon : Float = 0.toFloat()
        var endXIcon : Float = 0.toFloat()
        var endYIcon : Float = 0.toFloat()
    }

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?) : View?{

        val view : View = inflater.inflate(
            R.layout.fragment_quick,
            container,
            false
        )

        mCanvas = view.findViewById(R.id.fingerline)
        startX = 0.toFloat()
        startY =  0.toFloat()
        endX =  0.toFloat()
        endY =  0.toFloat()
        mCanvas!!.invalidate()
        var errorMessage = ""

        val imageView1 = view.findViewById<ImageView>(R.id.quick_1_1)
        val imageView2 = view.findViewById<ImageView>(R.id.quick_1_2)
        val imageView3 = view.findViewById<ImageView>(R.id.quick_1_3)
        val imageView4 = view.findViewById<ImageView>(R.id.quick_2_1)
        val imageView5 = view.findViewById<ImageView>(R.id.quick_2_2)
        val imageView6 = view.findViewById<ImageView>(R.id.quick_2_3)
        val imageView7 = view.findViewById<ImageView>(R.id.quick_3_1)
        val imageView8 = view.findViewById<ImageView>(R.id.quick_3_2)
        val imageView9 = view.findViewById<ImageView>(R.id.quick_3_3)
        val imageView10 = view.findViewById<ImageView>(R.id.quick_4_1)
        val imageView11 = view.findViewById<ImageView>(R.id.quick_4_2)
        val imageView12 = view.findViewById<ImageView>(R.id.quick_4_3)
        val imageView13 = view.findViewById<ImageView>(R.id.quick_5_1)
        val imageView14 = view.findViewById<ImageView>(R.id.quick_5_2)
        val imageView15 = view.findViewById<ImageView>(R.id.quick_5_3)

        val listener = View.OnTouchListener(function = { view, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_MOVE) {
                val iconLoc = IntArray(2)
                view.getLocationOnScreen(iconLoc)
                startXIcon = iconLoc[0].toFloat()
                startYIcon = iconLoc[1].toFloat()

                val linearLayoutLoc = IntArray(2)
                Layout1.getLocationOnScreen(linearLayoutLoc)
                val xLayout = linearLayoutLoc[0]
                val yLayout = linearLayoutLoc[1]
                startIcon = view.tag.toString()
                endIcon = ""

                startX = startXIcon + view.width/2
                startY = (startYIcon-yLayout + view.height/2)
                endX = motionEvent.rawX
                endY = motionEvent.rawY - yLayout

                mCanvas!!.invalidate()
            }
            if(motionEvent.action == MotionEvent.ACTION_UP){
                for (child in Layout1.children){
                    if(child is ViewGroup){
                        val vg : ViewGroup = child as ViewGroup
                        for(child2 in vg.children){
                            if(child2.tag != null){

                                val iconLoc = IntArray(2)
                                child2.getLocationOnScreen(iconLoc)
                                endXIcon = iconLoc[0].toFloat()
                                endYIcon = iconLoc[1].toFloat()

                                val linearLayoutLoc = IntArray(2)
                                Layout1.getLocationOnScreen(linearLayoutLoc)
                                val xLayout = linearLayoutLoc[0]
                                val yLayout = linearLayoutLoc[1]
                                if((endX >= endXIcon) and
                                    (endX <= (endXIcon + child2.width)) and
                                    (endY + yLayout >= endYIcon) and
                                    (endY + yLayout <= endYIcon + child2.height)){

                                    endIcon = child2.tag.toString()

                                    for(favorite in commutesList!!.favoritesItemsList){
                                        if (favorite.name == startIcon){
                                            startIconAddress = favorite.address
                                        }
                                        if (favorite.name == endIcon){
                                            endIconAddress = favorite.address
                                        }
                                    }

                                    var cal = Calendar.getInstance()
                                    val dateSetListener =
                                        DatePickerDialog.OnDateSetListener { datePicker,
                                                                             year, month, day ->
                                            cal.set(Calendar.YEAR, year)
                                            cal.set(Calendar.MONTH, month)
                                            cal.set(Calendar.DAY_OF_MONTH, day)
                                            chooseDate =
                                                SimpleDateFormat("YYYY-MM-dd")
                                                    .format(cal.time)

                                            cal = Calendar.getInstance()
                                            val timeSetListener =
                                                TimePickerDialog.OnTimeSetListener { timePicker,
                                                                                     hour, minute ->
                                                    cal.set(Calendar.HOUR_OF_DAY, hour)
                                                    cal.set(Calendar.MINUTE, minute)
                                                    chooseTime =
                                                        SimpleDateFormat("HH:mm")
                                                            .format(cal.time) + ":00"

                                                    commuteName = startIcon + "To" + endIcon
                                                    val newCommute = Commute()

                                                    val arrivalDate = chooseDate.toString()
                                                    val arrivalTime = chooseTime.toString()

                                                    val format = "yyyy-MM-dd hh:mm:ss"
                                                    val sdf = SimpleDateFormat(format)

                                                    val arrivalDateTime = arrivalDate + " " + arrivalTime
                                                    val timeArrival = sdf.parse(arrivalDateTime)
                                                    val timeNow = Calendar.getInstance().time

                                                    errorMessage = ""

                                                    if(commuteName.toString() == "")
                                                        errorMessage = "Name of commute is missing"
                                                    else if((startIconAddress == null) or (startIconAddress==""))
                                                        errorMessage = "Starting location is missing"
                                                    else if((endIconAddress == null) or (endIconAddress==""))
                                                        errorMessage = "Destination is missing"
                                                    else if(arrivalDate == "")
                                                        errorMessage = "Date information is missing"
                                                    else if(arrivalTime == "")
                                                        errorMessage = "Time information is missing"
                                                    else if(timeArrival <= timeNow){
                                                        errorMessage = "Please chose a later date and/or time"
                                                    } else {
                                                        newCommute.name = commuteName.toString()
                                                        newCommute.start = startIcon!!
                                                        newCommute.start_address = startIconAddress!!
                                                        newCommute.arrival = endIcon!!
                                                        newCommute.arrival_address = endIconAddress!!

                                                        val time = arrivalTime.split(":")
                                                        val date = arrivalDate.split("-")
                                                        newCommute.arrival_time_short =
                                                            "on " + date[2] + "." + date[1] + "." + date[0] +
                                                                    ", at " + time[0] + ":" + time[1]
                                                        newCommute.arrival_time_long =
                                                            arrivalDate +
                                                                    " " + arrivalTime

                                                        errorMessage = "Commute added"
                                                        commutesList!!.commutesItemsList.add(newCommute)

                                                        val pos = commutesList!!.commutesItemsList.size - 1
                                                        MainActivity.mGoogleAPI!!.requestRoute(
                                                            "Activity", pos, true
                                                        )

                                                        var prevPos =
                                                            FragmentCommutes.mAdapter!!.previousPosition
                                                        if(commutesList!!.commutesItemsList.count() < 2){
                                                            prevPos = -1
                                                        }
                                                        if((prevPos != -1) and
                                                            (prevPos < FragmentCommutes.mAdapter!!.commutesItemsList.size)
                                                        ) {
                                                            FragmentCommutes.mAdapter!!.viewLayouts(
                                                                visibleLayoutButtons = false,
                                                                visibleLayoutExtended = false,
                                                                pos = FragmentCommutes.mAdapter!!.previousPosition
                                                            )
                                                        }
                                                        source[0] = 1
                                                        mListener!!.onFragmentInteraction(3, source)
                                                    }
                                                    if(errorMessage != ""){
                                                        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
                                                        source[0] = 1
                                                        mListener!!.onFragmentInteraction(3, source)
                                                    }
                                                }
                                            val timeDialog = TimePickerDialog(
                                                context,
                                                timeSetListener,
                                                cal.get(Calendar.HOUR_OF_DAY),
                                                cal.get(Calendar.MINUTE),
                                                true
                                            )
                                            timeDialog.show()
                                        }
                                        DatePickerDialog(
                                            context,
                                            dateSetListener,
                                            cal.get(Calendar.YEAR),
                                            cal.get(Calendar.MONTH),
                                            cal.get(Calendar.DAY_OF_MONTH)
                                        ).show()

                                        if((startXIcon > endXIcon)){
                                            endX =
                                                (endXIcon + (child2.width) / 2 + (25 * this.resources.displayMetrics.density) + 20)
                                            endY = (endYIcon - yLayout + child2.height / 2)
                                            mCanvas!!.invalidate()
                                        }
                                        if((startXIcon < endXIcon)){
                                            endX =
                                                (endXIcon + (child2.width) / 2 - (25 * this.resources.displayMetrics.density) - 20)
                                            endY = (endYIcon - yLayout + child2.height / 2)
                                            mCanvas!!.invalidate()
                                        }

                                        if((startXIcon == endXIcon) and (startYIcon < endYIcon)){
                                            endX = (endXIcon + (child2.width) / 2)
                                            endY = (endYIcon - yLayout)
                                            mCanvas!!.invalidate()
                                        }

                                        if((startXIcon == endXIcon) and (startYIcon > endYIcon)){
                                            endX = (endXIcon + (child2.width) / 2)
                                            endY = (endYIcon - yLayout + child2.height)
                                            mCanvas!!.invalidate()
                                        }
                                        break
                                    }
                            }
                        }
                    }
                }
                if(errorMessage!="Commute added"){
                    startX = 0.toFloat()
                    startY =  0.toFloat()
                    endX =  0.toFloat()
                    endY =  0.toFloat()
                    mCanvas!!.invalidate()
                }
            }

            true
        })

        val listener3 = View.OnClickListener(function = { view -> true } )

        // Declared in our activity_shapes_view.xml file.
        imageView1.setOnTouchListener(listener)
        imageView2.setOnTouchListener(listener)
        imageView3.setOnTouchListener(listener)
        imageView4.setOnTouchListener(listener)
        imageView5.setOnTouchListener(listener)
        imageView6.setOnTouchListener(listener)
        imageView7.setOnTouchListener(listener)
        imageView8.setOnTouchListener(listener)
        imageView9.setOnTouchListener(listener)
        imageView10.setOnTouchListener(listener)
        imageView11.setOnTouchListener(listener)
        imageView12.setOnTouchListener(listener)
        imageView13.setOnTouchListener(listener)
        imageView14.setOnTouchListener(listener)
        imageView15.setOnTouchListener(listener)

        imageView1.setOnClickListener(listener3)
        imageView2.setOnClickListener(listener3)
        imageView3.setOnClickListener(listener3)
        imageView4.setOnClickListener(listener3)
        imageView5.setOnClickListener(listener3)
        imageView6.setOnClickListener(listener3)

        return view
    }
    override fun onAttach(context : Context){
        super.onAttach(context)
        if(context is FragmentCommutesEdit.OnFragmentInteractionListener){
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

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : IntArray)
    }
}