package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
/* *************************************************************** */

/* FragmentQuick ************************************************* */
/* Contains the quick route selection tools ********************** */
/* Contained in its standalone fragment ************************** */
class FragmentQuick : Fragment(){

    private val fragmentID = 6

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?) : View?{

        // return view
        return inflater.inflate(
            R.layout.fragment_quick,
            container,
            false
        )
    }
}