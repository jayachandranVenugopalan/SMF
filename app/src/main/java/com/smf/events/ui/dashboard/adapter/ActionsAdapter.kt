package com.smf.events.ui.dashboard.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.R
import com.smf.events.ui.dashboard.model.MyEvents

class ActionsAdapter:RecyclerView.Adapter<ActionsAdapter.ActionsViewHolder>() {

    private var myEventsList = ArrayList<MyEvents>()



    inner class ActionsViewHolder:RecyclerView.ViewHolder,View.OnClickListener {

         private var actionsNum:TextView?=null
        var actionTitle:TextView?=null
        constructor(view: View) : super(view) {
            actionsNum=view!!.findViewById<TextView>(R.id.action_numbers)
            actionTitle=view!!.findViewById<TextView>(R.id.actions_list)
            view.setOnClickListener(this)
        }

        override fun onClick(view: View?) {

            var actions=view!!.findViewById<TextView>(R.id.actions_list)

             var actionsName=  actions.text.toString()
            Log.d("TAG", "onClick:  $actionsName")
        }

        fun onBind(myEvents: MyEvents) {
            actionsNum?.text = myEvents.numberText
            actionTitle?.text =  myEvents.titleText

        }


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ActionsAdapter.ActionsViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.actions_status_dashboard_cardview, parent, false)
        return ActionsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActionsAdapter.ActionsViewHolder, position: Int) {
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
}