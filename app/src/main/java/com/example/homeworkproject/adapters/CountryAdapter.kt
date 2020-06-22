package com.example.homeworkproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homeworkproject.R
import com.example.homeworkproject.models.CountryModel
import kotlinx.android.synthetic.main.countries.view.*

class CountryAdapter(var countries:MutableList<CountryModel>): RecyclerView.Adapter<CountryAdapter.Viewholder>() {

    var selected=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        return Viewholder(LayoutInflater.from(parent.context).inflate(R.layout.countries,parent,false))
    }

    override fun getItemCount()=countries.size

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.onbind()
    }
    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var model:CountryModel
        fun onbind(){
            model=countries[adapterPosition]
            itemView.countrieid.text=model.name
            if(adapterPosition==selected) itemView.doneicon.visibility= View.VISIBLE
            else itemView.doneicon.visibility= View.INVISIBLE
            itemView.countrieid.setOnClickListener(){
                selected=adapterPosition
                notifyDataSetChanged()
            }
        }
    }
}