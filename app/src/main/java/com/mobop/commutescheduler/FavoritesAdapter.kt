package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.app.PendingIntent.getActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/* *************************************************************** */

/* FavoritesAdapter ********************************************** */
/* Adapter for the recycler view managing the list of favorites ** */
/* Contained in FragmentFavorites ******************************** */
class FavoritesAdapter(
    mRecyclerView: RecyclerView,
    viewRes : Int,
    favoritesItemsList: ArrayList<Favorite>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private lateinit var view : View

    var favoritesItemsList : ArrayList<Favorite>
    var previousPosition: Int = 0

    private var viewRes : Int = 0
    private var mRecyclerView: RecyclerView? = null


    init{
        this.favoritesItemsList = favoritesItemsList
        this.viewRes = viewRes
        this.mRecyclerView = mRecyclerView
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ViewHolder{

        view = LayoutInflater.from(parent.context).inflate(
            R.layout.element_favorite,
            parent,
            false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder : RecyclerView.ViewHolder,
        position : Int){

        val viewHolder = holder as ViewHolder
        viewHolder.bind(
            view,
            this.favoritesItemsList,
            position)
    }

    override fun getItemCount() : Int{
        return favoritesItemsList.count()
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val title : TextView
        val address : TextView

        val layoutFavorite : ConstraintLayout =
            view.findViewById(R.id.element_favorite) as ConstraintLayout

        init{
            title = view.findViewById(R.id.favorite_name) as TextView
            address = view.findViewById(R.id.favorite_address) as TextView
        }

        fun bind(
            view : View,
            favoritesItemsList : ArrayList<Favorite>,
            position : Int) {
            val itemInList = favoritesItemsList[position]

            if (position % 2 == 0){
                layoutFavorite.setBackgroundColor(
                    ContextCompat.getColor(view.context, R.color.colorCommutesEven))
            } else{
                layoutFavorite.setBackgroundColor(
                    ContextCompat.getColor(view.context, R.color.colorCommutesOdd))
            }

            if (itemInList != null) {
                val elementTitle = itemInList.name
                title.text = elementTitle
                val elementAddress = itemInList.address
                address.text = elementAddress
            }
        }
    }
}