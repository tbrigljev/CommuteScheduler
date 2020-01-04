package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
/* *************************************************************** */

/* FavoritesAdapter ********************************************** */
/* Adapter for the recycler view managing the list of favorites ** */
/* Contained in FragmentFavorites ******************************** */
class FavoritesAdapter(
    mRecyclerView: RecyclerView,
    viewRes : Int,
    favoritesItemsList: ArrayList<Favorite>,
    private val touchListener : (Int, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var view: View

    var favoritesItemsList: ArrayList<Favorite>
    var previousPosition: Int = 0

    private var viewRes: Int = 0
    private var mRecyclerView: RecyclerView? = null

    init {
        this.favoritesItemsList = favoritesItemsList
        this.viewRes = viewRes
        this.mRecyclerView = mRecyclerView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int) :
            ViewHolder {

        view = LayoutInflater.from(parent.context).inflate(
            R.layout.element_favorite,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int){

        val viewHolder = holder as ViewHolder
        viewHolder.bind(
            view,
            this.favoritesItemsList,
            position,
            touchListener
        )

        holder.editFavorite(favoritesItemsList
            .indexOf(favoritesItemsList[position]),
            1)
        holder.deleteFavorite(favoritesItemsList.
            indexOf(favoritesItemsList[position]),
            3)
    }

    override fun getItemCount(): Int {
        return favoritesItemsList.count()
    }

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val title: TextView
        private val address: TextView

        private val layoutFavorite: ConstraintLayout =
            view.findViewById(R.id.element_favorite)
                    as ConstraintLayout

        val favoritesButtons : ConstraintLayout =
            view.findViewById(R.id.favorites_buttons)
                    as  ConstraintLayout

        private val editButton =
            view.findViewById(R.id.favorite_edit)
                    as ImageButton
        private val deleteButton =
            view.findViewById(R.id.favorite_delete)
                    as ImageButton

        fun editFavorite(pos : Int, action : Int){
            editButton.setOnClickListener{
                touchListener(pos, action) }
        }
        fun deleteFavorite(pos : Int, action : Int){
            deleteButton.setOnClickListener{
                touchListener(pos, action) }
        }

        init {
            title = view.findViewById(R.id.favorite_name)
                    as TextView
            address = view.findViewById(R.id.favorite_address)
                    as TextView

            favoritesButtons.visibility = View.GONE
        }

        fun bind(
            view: View,
            favoritesItemsList: ArrayList<Favorite>,
            position: Int,
            touchListener : (Int, Int) -> Unit){
            val itemInList = favoritesItemsList[position]

            if(position % 2 == 0) {
                layoutFavorite.setBackgroundColor(
                    ContextCompat.getColor(view.context,
                        R.color.colorCommutesEven)
                )
            } else {
                layoutFavorite.setBackgroundColor(
                    ContextCompat.getColor(view.context,
                        R.color.colorCommutesOdd)
                )
            }

            if(itemInList != null) {
                val elementTitle = itemInList.name
                title.text = elementTitle
                val elementAddress = itemInList.address
                address.text = elementAddress

                itemView.setOnTouchListener(
                    object : OnSwipeTouchListener(){
                    override fun onClick(){
                        if(previousPosition != adapterPosition){
                            try {
                                val viewPrevious : View =
                                    mRecyclerView!!
                                        .findViewHolderForAdapterPosition(
                                        previousPosition
                                    )!!.itemView
                                val layoutButtonsPrevious : ConstraintLayout =
                                    viewPrevious
                                        .findViewById(R.id.favorites_buttons)
                                            as ConstraintLayout

                                layoutButtonsPrevious.visibility = View.GONE
                                previousPosition = adapterPosition

                            } catch (e: Exception) {
                                Log.i("FavoritesClickException", e.toString())
                            }
                        }

                        if(favoritesButtons.visibility == View.VISIBLE){
                            favoritesButtons.visibility = View.GONE
                        } else if (favoritesButtons.visibility == View.GONE){
                            favoritesButtons.visibility = View.VISIBLE
                        }
                    }
                })
            }
        }
    }

    fun removeAt(position: Int){
        var temp:Long = favoritesItemsList[position].pid

        favoritesItemsList.removeAt(position)
        previousPosition = 0
        notifyItemRemoved(position)


        doAsync{
            commutesList!!
                .database
                .deleteItem(temp)

        }

    }

    fun viewLayouts(visibleLayoutButtons : Boolean, pos : Int){
        val viewPos : View =
            mRecyclerView!!
                .findViewHolderForAdapterPosition(pos)!!
                .itemView

        val layoutButtonsPos : ConstraintLayout =
            viewPos.findViewById(R.id.favorites_buttons)
                    as ConstraintLayout

        if (visibleLayoutButtons){
            layoutButtonsPos.visibility = View.VISIBLE
        } else {
            layoutButtonsPos.visibility = View.GONE
        }
    }
}