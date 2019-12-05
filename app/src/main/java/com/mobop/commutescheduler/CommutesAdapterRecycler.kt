package com.mobop.commutescheduler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommutesAdapterRecycler(
    viewRes: Int,
    commutesItemsList2: ArrayList<CommutesItemsElement>,
    val clickListener: (Int) -> Unit):
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var commutesItemsList: ArrayList<CommutesItemsElement>
    private var viewRes: Int = 0

    init{
        this.commutesItemsList = commutesItemsList2
        this.viewRes = viewRes
    }

    /*
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :
            RecyclerView.ViewHolder {
        return {
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.element_commute_simple,
                    parent,
                    false
                )
            )
        }
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            CommutesAdapterRecycler.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.element_commute_simple, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder : RecyclerView.ViewHolder,
                                  position : Int){
        val ViewHolder = holder as ViewHolder
        ViewHolder.bind(this.commutesItemsList, position, clickListener)
    }

    override fun getItemCount() : Int{
        return commutesItemsList.count()
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){

        val title : TextView
        val start : TextView
        val end : TextView
        val time : TextView

        init{
            title = view.findViewById(R.id.element_simple_title) as TextView
            start = view.findViewById(R.id.element_simple_start) as TextView
            end = view.findViewById(R.id.element_simple_end) as TextView
            time = view.findViewById(R.id.element_simple__time) as TextView
        }

        fun bind(commutesItemsList: ArrayList<CommutesItemsElement>,
                 position : Int, clickListener : (Int) -> Unit){
            val commutesItems = commutesItemsList[position]
            if (commutesItems != null){
                val elementTitle = commutesItems.commutesName
                title.setText(elementTitle)
                val elementStart = commutesItems.commutesStart
                start.setText(elementStart)
                val elementEnd = commutesItems.commutesEnd
                end.setText(elementEnd)
                val elementTime = commutesItems.commutesTime
                time.setText(elementTime)
            }
            itemView.setOnClickListener{ clickListener(position) }
        }
    }
}