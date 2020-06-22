package com.example.homeworkproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.homeworkproject.R
import com.example.homeworkproject.models.MainModel
import kotlinx.android.synthetic.main.items_layout.view.*

class Adapter(val itemsList:MutableList<MainModel>): RecyclerView.Adapter<Adapter.ViewHolder>() {

    var pos:Int=-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.items_layout,parent,false))
    }

    override fun getItemCount()=itemsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onbind()
    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private lateinit var model: MainModel
        fun onbind(){
            model=itemsList[adapterPosition]
            itemView.description.text=model.description
            if(adapterPosition==pos) itemView.adressactive.visibility= View.VISIBLE
            else itemView.adressactive.visibility= View.INVISIBLE
            itemView.description.setOnClickListener(){
                pos=adapterPosition
                notifyDataSetChanged()
            }
        }
    }
}