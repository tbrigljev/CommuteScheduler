package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.ClipData
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.fragment_edit.*

/* *************************************************************** */


/* FragmentFavorites ********************************************* */
/* Contains the list of favorites and the related buttons ******** */
/* Contained in FragmentHome and in its standalone fragment ****** */
class FragmentFavorites : Fragment(){
    companion object{
        var mRecyclerView : RecyclerView? = null
        var mAdapter : FavoritesAdapter?= null
    }

    private var mListener : OnFragmentInteractionListener? = null

    /*private var mItemTouchHelper : ItemTouchHelper? = null*/

    private val fragmentID = 4
    //private var source = 0
    private var source : IntArray = intArrayOf(0,0)

    private lateinit var returnFavoritesButton : ImageButton

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savecInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.fragment_favorites,
            container,
            false
        )

        returnFavoritesButton =
            view.findViewById(R.id.favorites_button_return)
                    as ImageButton
        returnFavoritesButton.setOnClickListener{
            doFavoritesReturn(fragmentID)
        }

        /* Creation of the RecyclerView, LayoutManager and Adapter */
        mRecyclerView = view.findViewById(R.id.favorites_list)

        var mLayoutManager = LinearLayoutManager(context)
        mRecyclerView!!.layoutManager = mLayoutManager

        mAdapter =
            FavoritesAdapter(mRecyclerView!!,
                R.layout.element_favorite,
                favoritesList!!.favoritesItemsList
            )

        mRecyclerView!!.adapter = mAdapter

        return view
    }

    override fun onAttach(context : Context){
        super.onAttach(context)
        if(context is OnFragmentInteractionListener){
            mListener = context
        } else{
            throw RuntimeException(context!!.toString() +
                    " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach(){
        super.onDetach()
        mListener = null
    }

    private fun doFavoritesReturn(fragmentCaller : Int){
        if (mListener != null){
            source[0] = 0
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : IntArray)
    }
}