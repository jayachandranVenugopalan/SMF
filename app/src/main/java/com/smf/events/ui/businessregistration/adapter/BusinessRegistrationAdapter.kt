package com.smf.events.ui.businessregistration.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.R
import java.util.concurrent.TimeoutException

class BusinessRegistrationAdapter:
    RecyclerView.Adapter<BusinessRegistrationAdapter.BusinessRegistrationViewHolder>() {
  inner  class BusinessRegistrationViewHolder(view: View):RecyclerView.ViewHolder(view) {
      var expand:Boolean=false
var businessRegName=view.findViewById<ConstraintLayout>(R.id.business_reg_layout)
      var expandablelayout=view.findViewById<ConstraintLayout>(R.id.business_reg_exp_layout)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BusinessRegistrationAdapter.BusinessRegistrationViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.business_registration_card_view, parent, false)
        return BusinessRegistrationViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: BusinessRegistrationAdapter.BusinessRegistrationViewHolder,
        position: Int,
    ) {
        with(holder){
            businessRegName.setOnClickListener {
                expandablelayout.visibility   = if (expand) View.VISIBLE else View.GONE
                expand = !expand
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
    return 1
    }
}