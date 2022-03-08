package com.smf.events.ui.dashboard.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.R
import com.smf.events.ui.dashboard.model.MyEvents

class StatusAdaptor:RecyclerView.Adapter<StatusAdaptor.StatusViewHolder>() {
    private var myEventsList = ArrayList<MyEvents>()
    inner class StatusViewHolder(val view: View):RecyclerView.ViewHolder(view) {
      var  actionsNum=view!!.findViewById<TextView>(R.id.action_numbers)
       var actionTitle=view!!.findViewById<TextView>(R.id.actions_list)
        fun onBind(myEvents: MyEvents) {
            actionsNum?.text = myEvents.numberText
            actionTitle?.text =  myEvents.titleText

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): StatusAdaptor.StatusViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.actions_status_dashboard_cardview, parent, false)
        return StatusViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StatusAdaptor.StatusViewHolder, position: Int) {
        holder.onBind(myEventsList[position])
    }
    //Method For Refreshing Invoices
    @SuppressLint("NotifyDataSetChanged")
    fun refreshItems(invoice: List<MyEvents>) {
        myEventsList.clear()
        myEventsList.addAll(invoice)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return myEventsList.size
    }

}