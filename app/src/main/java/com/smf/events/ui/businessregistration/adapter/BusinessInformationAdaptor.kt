package com.smf.events.ui.businessregistration.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.R

class BusinessInformationAdaptor:
    RecyclerView.Adapter<BusinessInformationAdaptor.BusinessInfoViewHolder>() {
    inner class BusinessInfoViewHolder(val view: View):RecyclerView.ViewHolder(view) {
        var expand : Boolean = false
        var businessInfo=view.findViewById<TextView>(R.id.tx_businessname)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BusinessInformationAdaptor.BusinessInfoViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.business_information_cardview, parent, false)
        return BusinessInfoViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: BusinessInformationAdaptor.BusinessInfoViewHolder,
        position: Int,
    ) {
        with(holder){
            var exp= view.findViewById<ConstraintLayout>(R.id.expandablelayout)

            businessInfo.setOnClickListener {
                exp.visibility   = if (expand) View.VISIBLE else View.GONE
                expand = !expand
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }
}