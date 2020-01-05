package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
/* *************************************************************** */

/* FragmentMap *************************************************** */
/* Contains the map element and the related buttons ************** */
/* Contained in FragmentHome and in its standalone fragment ****** */

class FragmentMap(screen : Int) :
    Fragment(), OnMapReadyCallback{

    companion object {
        lateinit var mMap : GoogleMap
        lateinit var mapCommuteText : ConstraintLayout
        lateinit var mapFieldCommuteNText : TextView
        lateinit var mapFieldCommuteName : TextView
        lateinit var mapFieldCommuteDText : TextView
        lateinit var mapFieldCommuteDuration : TextView
        lateinit var mapOverlayStart : TextView
        lateinit var mapOverlayStartTime : TextView
        lateinit var mapOverlayDestination : TextView
        lateinit var mapOverlayDestinationTime : TextView
        lateinit var mapOverlayLength : TextView
    }

    private var mListener : OnFragmentInteractionListener? = null


    private var fragmentID = 1
    private var source : IntArray = intArrayOf(0, 0)

    private lateinit var returnMapButton : ImageButton
    private lateinit var addMapButton : ImageButton
    private lateinit var enhanceMapButton : ImageButton
    private lateinit var overlayMapButton : ImageButton

    private lateinit var viewTrafficMapCheck : CheckBox

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.fragment_map,
            container,
            false
        )

        /* Set Fragment for Google Map *************************** */
        val googleMapFragment = childFragmentManager
            .findFragmentById(R.id.google_map)
                as SupportMapFragment
        googleMapFragment.getMapAsync(this)

        mapCommuteText =
            view.findViewById(R.id.fragment_map_text)
                    as ConstraintLayout
        mapFieldCommuteNText =
            view.findViewById(R.id.map_text_commute_name)
                    as TextView
        mapFieldCommuteName =
            view.findViewById(R.id.map_field_commute_name)
                    as TextView
        mapFieldCommuteDText =
            view.findViewById(R.id.map_text_commute_duration)
                    as TextView
        mapFieldCommuteDuration =
            view.findViewById(R.id.map_field_commute_duration)
                    as TextView

        mapFieldCommuteNText.text = getString(R.string.no_commute)
        mapFieldCommuteName.visibility = View.GONE
        mapFieldCommuteDText.visibility = View.GONE
        mapFieldCommuteDuration.visibility = View.GONE

        mapOverlayStart =
            view.findViewById(R.id.overlay_start_field)
                    as TextView
        mapOverlayStartTime =
            view.findViewById(R.id.overlay_start_time_field)
                    as TextView
        mapOverlayDestination =
            view.findViewById(R.id.overlay_destination_field)
                    as TextView
        mapOverlayDestinationTime =
            view.findViewById(R.id.overlay_destination_time_field)
                    as TextView
        mapOverlayLength =
            view.findViewById(R.id.overlay_length_field)
                    as TextView

        enhanceMapButton =
            view.findViewById(R.id.map_button_enhance)
                    as ImageButton
        addMapButton =
            view.findViewById(R.id.map_button_add)
                    as ImageButton
        returnMapButton =
            view.findViewById(R.id.map_button_return)
                    as ImageButton
        overlayMapButton =
            view.findViewById(R.id.map_button_overlay)
                    as ImageButton

        viewTrafficMapCheck =
            view.findViewById(R.id.view_traffic)
                as CheckBox

        val layoutOverlay : ConstraintLayout =
            view.findViewById(R.id.map_overlay)
                    as ConstraintLayout

        layoutOverlay.visibility = View.GONE

        enhanceMapButton.setOnClickListener{
            doMapEnhance(fragmentID)
        }
        addMapButton.setOnClickListener {
            doMapAdd(fragmentID)
        }
        returnMapButton.setOnClickListener{
            doMapReturn(fragmentID)
        }
        overlayMapButton.setOnClickListener{
            doMapOverlay(layoutOverlay)
        }

        viewTrafficMapCheck.setOnCheckedChangeListener{
                buttonView, isChecked ->
            mMap.isTrafficEnabled = isChecked
        }

        returnMapButton.visibility = View.GONE
        addMapButton.visibility = View.GONE
        enhanceMapButton.visibility = View.VISIBLE
        overlayMapButton.visibility = View.GONE

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

    private fun doMapOverlay(layoutOverlay : ConstraintLayout){
        when(layoutOverlay.visibility){
            View.GONE -> {
                layoutOverlay.visibility = View.VISIBLE
            }
            View.VISIBLE -> {
                layoutOverlay.visibility = View.GONE
            }
        }
    }

    private fun doMapEnhance(fragmentCaller : Int){
        if (mListener != null){
            source[0] = 0

            returnMapButton.visibility = View.VISIBLE
            addMapButton.visibility = View.VISIBLE
            enhanceMapButton.visibility = View.GONE

            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doMapReturn(fragmentCaller : Int){
        if (mListener != null){
            source[0] = 1
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doMapAdd(fragmentCaller : Int){
        if (mListener != null){
            source[0] = 2
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : IntArray)
    }

    /* *********************************************************** */
    /* onMapReady() ********************************************** */
    /* Manipulates the map once available ************************ */
    /* This callback is triggered when the map is ready to be used */
    /* This is where markers, lines or listeners are added ******* */
    /* This is where we can move the camera ********************** */
    /* If Google Play Services is not installed on the device, *** */
    /* *** the user will be prompter to install it inside of the * */
    /* *** SupportMapFragment ************************************ */
    /* The method is then only triggered after the user has ****** */
    /* *** installed Google Play Services and has returned to the  */
    /* *** app *************************************************** */
    /* *********************************************************** */
    override fun onMapReady(googleMap : GoogleMap){
        mMap = googleMap

        val suisse = LatLng(46.523278, 6.609954)
        mMap.addMarker(
            MarkerOptions()
                .position(suisse)
                .title("Suisse marker"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(suisse))
    }
}
