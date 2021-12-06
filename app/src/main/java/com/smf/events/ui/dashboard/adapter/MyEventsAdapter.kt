package com.smf.events.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.R
import com.smf.events.ui.dashboard.model.MyEvents

class MyEventsAdapter: RecyclerView.Adapter<MyEventsAdapter.MyEventViewHolder>() {

    private var myEventsList = ArrayList<MyEvents>()
    private var onClickListener: OnServiceClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEventViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.events_card_view, parent, false)
        return MyEventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyEventViewHolder, position: Int) {
        holder.eventName.text =" Sana Wedding"
        holder.eventDate.text =  "Jun 11"
        holder.eventStatus.text = "in bidding"
        // holder.onBind(myEventsList[position])

    }

    override fun getItemCount(): Int {
        return 10
    }
    inner class MyEventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var eventName = view.findViewById<TextView>(R.id.event_title)
        var eventDate = view.findViewById<TextView>(R.id.event_date)
        var eventStatus = view.findViewById<TextView>(R.id.event_status)

//        // Method For Fixing xml views and Values
//        fun onBind(myEvents: MyEvents) {
//            eventName.text = myEvents.eventName
//            eventDate.text =  myEvents.eventDate
//            eventStatus.text = myEvents.eventStatus
//
//        }
    }

    //Method For Refreshing Invoices
    fun refreshItems(invoice: List<MyEvents>) {
        myEventsList.clear()
        myEventsList.addAll(invoice)
        notifyDataSetChanged()
    }

    // Initializing Listener Interface
    fun setOnClickListener(listener: OnServiceClickListener) {
        onClickListener = listener
    }

    // Interface For Invoice Click Listener
    interface OnServiceClickListener {

    }

}