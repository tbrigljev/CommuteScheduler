package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.content.ClipData
import android.content.Context
import android.graphics.Color
import android.media.Image
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
import kotlinx.android.synthetic.main.fragment_commutes_edit.*

/* *************************************************************** */


/* FragmentFavorites ********************************************* */
/* Contains the list of favorites and the related buttons ******** */
/* Contained in its standalone fragment ************************** */
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
    private var empty : Boolean = true

    private lateinit var returnFavoritesButton : ImageButton
    private lateinit var addFavoritesButton : ImageButton
    private lateinit var emptyFavorites : ConstraintLayout
    private lateinit var emptyFavoritesAdd : ImageButton

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

        emptyFavorites =
            view.findViewById(R.id.favorites_empty_container)
                    as ConstraintLayout
        emptyFavoritesAdd =
            view.findViewById(R.id.favorites_button_empty)
                    as ImageButton

        addFavoritesButton =
            view.findViewById(R.id.favorites_button_add)
                    as ImageButton
        returnFavoritesButton =
            view.findViewById(R.id.favorites_button_return)
                    as ImageButton

        if(favoritesList!!.favoritesItemsList.count() < 1){
            empty = true
            addFavoritesButton.visibility = View.GONE
        } else{
            empty = false
            emptyFavorites.visibility = View.GONE
        }

        addFavoritesButton.setOnClickListener{
            doFavoritesAdd(fragmentID, empty)
        }
        emptyFavoritesAdd.setOnClickListener{
            doFavoritesAdd(fragmentID, empty)
        }
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
                favoritesList!!.favoritesItemsList,
                { partItem : Int, action : Int -> doFavoritesButtons(partItem, action) }
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


    private fun doFavoritesAdd(fragmentCaller : Int, empty : Boolean){
        if(mListener != null){
            mRecyclerView!!.adapter!!.notifyDataSetChanged()
            source[0] = 0
            if(commutesList!!.commutesItemsList.count() < 1){
                source[1] = -1
            }
            mListener!!.onFragmentInteraction(fragmentCaller, source)

            if(commutesList!!.commutesItemsList.count() > 0){
                emptyFavorites.visibility = View.GONE
                addFavoritesButton.visibility = View.VISIBLE
            }
        }
    }

    private fun doFavoritesReturn(fragmentCaller : Int){
        if (mListener != null){
            source[0] = 2
            mListener!!.onFragmentInteraction(fragmentCaller, source)
        }
    }

    private fun doFavoritesButtons(partItem : Int, action : Int){
        when(action){
            1 -> {
                if(mListener != null){
                    //layoutButtons.visibility = View.GONE
                    source = intArrayOf(action, partItem)
                    mListener!!.onFragmentInteraction(fragmentID, source)
                }
            }
            3 -> {
                mAdapter!!.viewLayouts(false, partItem)
                mAdapter!!.removeAt(partItem)

                mRecyclerView!!.adapter!!.notifyDataSetChanged()

                if(favoritesList!!.favoritesItemsList.count() < 1){
                    emptyFavorites.visibility = View.VISIBLE
                    addFavoritesButton.visibility = View.GONE
                }

                val text = "Favorite deleted"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(getActivity(), text, duration)
                toast.show()
            }
        }
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction(
            fragmentCaller : Int,
            fragmentState : IntArray)
    }
}