package com.mobop.commutescheduler

/* Import ******************************************************** */
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
/* *************************************************************** */

/* CommutesAdapter *********************************************** */
/* Adapter for the recycler view managing the list of commutes *** */
/* Contained in FragmentCommutes ********************************* */
class CommutesAdapter(
    viewRes : Int,
    commutesItemsList: ArrayList<Commute>,
    val clickListener : (Int) -> Unit) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var commutesItemsList : ArrayList<Commute>
    private var viewRes : Int = 0

    init{
        this.commutesItemsList = commutesItemsList
        this.viewRes = viewRes
    }

    override fun onCreateViewHolder(
        parent : ViewGroup,
        viewType : Int) :
            CommutesAdapter.ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(
                R.layout.element_commute_simple,
                parent,
                false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(
        holder : RecyclerView.ViewHolder,
        position : Int){
        val ViewHolder = holder as ViewHolder
        ViewHolder.bind(
            this.commutesItemsList,
            position,
            clickListener)
    }

    override fun getItemCount() : Int{
        return commutesItemsList.count()
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val title : TextView
        val start : TextView
        val end : TextView
        val time : TextView

        val layout : LinearLayout = view.findViewById(R.id.element_simple) as LinearLayout

        init{
            title =
                view.findViewById(R.id.element_simple_title)
                        as TextView
            start =
                view.findViewById(R.id.element_simple_start)
                        as TextView
            end =
                view.findViewById(R.id.element_simple_end)
                        as TextView
            time =
                view.findViewById(R.id.element_simple_time)
                        as TextView
        }

        fun bind(
            commutesItemsList : ArrayList<Commute>,
            position : Int,
            clickListener : (Int) -> Unit){
            val itemInList = commutesItemsList[position]

            if (position % 2 == 0){
                layout.setBackgroundColor(Color.RED)
            } else{
                layout.setBackgroundColor(Color.BLUE)
            }

            if (itemInList != null){
                val elementTitle = itemInList.name
                title.text = elementTitle
                val elementStart = itemInList.start
                start.text = elementStart
                val elementEnd = itemInList.arrival
                end.text = elementEnd
                val elementTime = itemInList.start_time
                time.text = elementTime
            }
            itemView.setOnClickListener{ clickListener(position) }
        }
    }
}