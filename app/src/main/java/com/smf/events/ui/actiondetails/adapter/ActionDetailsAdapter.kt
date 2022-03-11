package com.smf.events.ui.actiondetails.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.R
import com.smf.events.ui.dashboard.model.MyEvents

class ActionDetailsAdapter(val context: Context) :
    RecyclerView.Adapter<ActionDetailsAdapter.ActionDetailsViewHolder>() {

    private var myEventsList = ArrayList<MyEvents>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ActionDetailsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.event_details_card_view, parent, false)
        return ActionDetailsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActionDetailsViewHolder, position: Int) {
        holder.onBind(myEventsList[position])

    }



    override fun getItemCount(): Int {
        return myEventsList.size
    }

    //Method For Refreshing Invoices
    @SuppressLint("NotifyDataSetChanged")
    fun refreshItems(invoice: List<MyEvents>) {
        myEventsList.clear()
        myEventsList.addAll(invoice)
        notifyDataSetChanged()
    }

    inner class ActionDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var amount: TextView = view.findViewById(R.id.amount_text)
        var code: TextView = view.findViewById(R.id.code_text)


        fun onBind(myEvents: MyEvents) {
            amount.text = myEvents.numberText
            code.text = myEvents.titleText



        }


    }

}