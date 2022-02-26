package com.smf.events.ui.businessregistration.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.R

class OtherInformationAdapter:RecyclerView.Adapter<OtherInformationAdapter.OtherInfoViewHolder> (){
    inner class OtherInfoViewHolder(view: View): RecyclerView.ViewHolder(view){
var expand:Boolean=false
        var exp1=view.findViewById<ConstraintLayout>(R.id.expandablelayoutotherinfo)
        var otherInfoName=view.findViewById<TextView>(R.id.otherinfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherInfoViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.other_info_cardview, parent, false)
        return OtherInfoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OtherInfoViewHolder, position: Int) {
        with(holder){


            otherInfoName.setOnClickListener {
                exp1.visibility   = if (expand) View.VISIBLE else View.GONE
                expand = !expand
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
return 1
    }
}