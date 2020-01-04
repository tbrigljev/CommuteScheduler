package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import org.jetbrains.anko.doAsync

/* *************************************************************** */

/* FragmentFavoritesEdit ***************************************** */
/* Fragment reserved for the edition of favorite elements********* */
/* This is used for both editing existing elements and adding ones */

class FragmentFavoritesEdit(
    private var new : Boolean,
    private var pos : Int) :
    Fragment(){

    companion object {
        //var start: String = "Home"
        var name : String = ""
        //var arrival: String = "Route des Arsenaux 29, 1700 Fribourg, Switzerland"
        var address : String = ""
    }
    private var mListener : OnFragmentInteractionListener? = null

    private val fragmentID = 5
    private var source : IntArray = intArrayOf(0, 0)

    private lateinit var cancelEditButton : ImageButton
    private lateinit var validateEditButton : ImageButton

    private lateinit var favoriteName : EditText
    private lateinit var favoriteAddress : AutocompleteSupportFragment

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?) : View?{

        val view = inflater.inflate(
            R.layout.fragment_favorites_edit,
            container,
            false
        )
        MainActivity.mGoogleAPI!!.setActivityContext(
            this,
            getActivity()!!.getApplicationContext())

        cancelEditButton =
            view.findViewById(R.id.favorite_edit_button_cancel)
                    as ImageButton
        validateEditButton =
            view.findViewById(R.id.favorite_edit_button_validate)
                    as ImageButton

        favoriteName = view.findViewById(R.id.favorite_edit_title)

        favoriteAddress = childFragmentManager
            .findFragmentById(R.id.fragment_address)
                as AutocompleteSupportFragment
        favoriteAddress
            .setHint(getString(R.string.field_favorite_address))

        cancelEditButton.setOnClickListener{
            doEditCancel(fragmentID)
        }

        validateEditButton.setOnClickListener{
            doEditValidate(fragmentID)
        }

        if(new){
            favoriteName.setText("")
        } else {
            favoriteName.setText(commutesList!!.favoritesItemsList[pos].name)
            address = commutesList!!.favoritesItemsList[pos].address

            favoriteAddress.setText(address)
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
            favoriteName.setText("")

            hideKeyboard()

            source[0] = 0
            mListener!!.onFragmentInteraction(fragmentCaller, source)
            if ( pos >= 0 ){
                FragmentFavorites.mAdapter!!.viewLayouts(
                    false,
                    pos)
            }
        }
    }

    private fun doEditValidate(fragmentCaller : Int){
        if (mListener != null){

            var newFavorite = Favorite()

            newFavorite.name = favoriteName.text.toString()
            newFavorite.address = address

            lateinit var text : String

            if(newFavorite.name == "")
                text = "Name of favorite is missing"
            else if(newFavorite.address == "")
                text = "Address of favorite is missing"
            else {  //All minimum values introduced
                if(new){
                    text = "Favorite added"

                    commutesList!!.favoritesItemsList.add(newFavorite)
                    doAsync{
                        commutesList!!.database.insertAllFavorite(newFavorite)

                    }
                    var pos = commutesList!!.favoritesItemsList.size - 1

                    var prev_pos = FragmentFavorites.mAdapter!!.previousPosition
                    if(commutesList!!.favoritesItemsList.count() < 2){
                        prev_pos = -1
                    }
                    if((prev_pos != -1) and
                        (prev_pos < FragmentFavorites.mAdapter!!.favoritesItemsList.size)){
                        FragmentFavorites.mAdapter!!.viewLayouts(
                            false,
                            FragmentFavorites.mAdapter!!.previousPosition
                        )
                    }
                }
                else{
                    text = "Favorite modified"
                    commutesList!!
                        .favoritesItemsList[pos]
                        .name = newFavorite.name
                    commutesList!!
                        .favoritesItemsList[pos]
                        .address = newFavorite.address

                    doAsync{ commutesList!!
                        .database
                        .updateAllFavorite(commutesList!!
                            .favoritesItemsList[pos])}

                    FragmentFavorites.mAdapter!!
                        .viewLayouts(
                            false,
                            pos)
                }
            }

            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(context, text, duration)
            toast.show()

            FragmentFavorites.mRecyclerView!!.adapter!!.notifyDataSetChanged()

            if((text == "Favorite added")
                or (text == "Favorite modified")){
                source[0] = 1
                source[1] = pos
                mListener!!.onFragmentInteraction(fragmentCaller, source)
                favoriteName.setText("")
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