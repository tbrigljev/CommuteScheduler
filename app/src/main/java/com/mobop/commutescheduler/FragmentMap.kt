package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.textfield.TextInputEditText

/* *************************************************************** */

/* FragmentMap *************************************************** */
/* Contains the map element and the related buttons ************** */
/* Contained in FragmentHome and in its standalone fragment ****** */
class FragmentMap(screen : Int) : Fragment(), OnMapReadyCallback{

    companion object {
        lateinit var mMap: GoogleMap
        var start: String = "Fribourg"
        var arrival: String = "Granges-Paccot"
    }
    private var mListener : OnFragmentInteractionListener? = null

    private val mapScreen = screen

    private var fragmentID = 1
    private var source = 0

    private lateinit var returnMapButton : ImageButton
    private lateinit var addMapButton : ImageButton
    private lateinit var enhanceMapButton : ImageButton
    private lateinit var overlayMapButton : ImageButton
    private lateinit var goMapButton : Button
    private lateinit var arriveDateMapInput : TextInputEditText
    private lateinit var viewTrafficMapCheck : CheckBox


    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savecInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.fragment_map,
            container,
            false
        )

        MainActivity.mGoogleAPI!!.setActivityContext(this, getActivity()!!.getApplicationContext())

        /* Set Fragment for Google Map *************************** */
        val googleMapFragment = childFragmentManager
            .findFragmentById(R.id.google_map)
                as SupportMapFragment
        googleMapFragment.getMapAsync(this)

        enhanceMapButton =
            view.findViewById(R.id.map_button_enhance) as ImageButton
        addMapButton =
            view.findViewById(R.id.map_button_add) as ImageButton
        returnMapButton =
            view.findViewById(R.id.map_button_return) as ImageButton
        overlayMapButton =
            view.findViewById(R.id.map_button_overlay) as ImageButton
        goMapButton =
            view.findViewById(R.id.button_go) as Button
        arriveDateMapInput=
            view.findViewById(R.id.text_date) as TextInputEditText
        viewTrafficMapCheck = view.findViewById(R.id.view_traffic) as CheckBox

        val layoutOverlay : ConstraintLayout =
            view.findViewById(R.id.map_overlay_optional) as ConstraintLayout

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
        goMapButton.setOnClickListener{view ->
            var arrival_time = arriveDateMapInput!!.text
            MainActivity.mGoogleAPI!!.requestRoute(
                "Activity",
                start,
                arrival,
                arrival_time.toString())
        }
        viewTrafficMapCheck.setOnCheckedChangeListener{
                buttonView, isChecked ->
            if(isChecked){ mMap.isTrafficEnabled = true }
            else { mMap.isTrafficEnabled = false }
        }

        returnMapButton.visibility = View.GONE
        addMapButton.visibility = View.GONE
        enhanceMapButton.visibility = View.VISIBLE

        /*
        when(mapScreen){
            0 -> {
                returnMapButton.visibility = View.VISIBLE
                addMapButton.visibility = View.VISIBLE
                enhanceMapButton.visibility = View.GONE
            }
            1 -> {
                returnMapButton.visibility = View.GONE
                addMapButton.visibility = View.GONE
                enhanceMapButton.visibility = View.VISIBLE
            }
        }*/

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
            source = 0

            returnMapButton.visibility = View.VISIBLE
            addMapButton.visibility = View.VISIBLE
            enhanceMapButton.visibility = View.GONE

            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    fun doMapReturn(fragmentCaller : Int){
        if (mListener != null){
            source = 1

            /*
            returnMapButton.visibility = View.GONE
            addMapButton.visibility = View.GONE
            enhanceMapButton.visibility = View.VISIBLE*/

            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doMapAdd(fragmentCaller : Int){
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

        val suisse = LatLng(46.204391, 6.143158)
        mMap.addMarker(
            MarkerOptions()
                .position(suisse)
                .title("Suisse marker"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(suisse))
    }
}
