package com.smf.events.ui.actiondetails.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.R
import com.smf.events.rxbus.RxBus
import com.smf.events.rxbus.RxEvent
import com.smf.events.ui.actiondetails.model.ActionDetails
import java.time.Month

class ActionDetailsAdapter(val context: Context) :
    RecyclerView.Adapter<ActionDetailsAdapter.ActionDetailsViewHolder>() {

    private var myEventsList = ArrayList<ActionDetails>()

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
        holder.details(myEventsList[position], holder)


    }


    override fun getItemCount(): Int {
        return myEventsList.size
    }

    //Method For Refreshing Invoices
    @SuppressLint("NotifyDataSetChanged")
    fun refreshItems(invoice: List<ActionDetails>) {
        myEventsList.clear()
        myEventsList.addAll(invoice)
        notifyDataSetChanged()
    }

    inner class ActionDetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var eventName: TextView = view.findViewById(R.id.event_title_text)
        var amount: TextView = view.findViewById(R.id.amount_text)
        var code: TextView = view.findViewById(R.id.code_text)
        var eventType: TextView = view.findViewById(R.id.event_type_text)
        var eventDate: TextView = view.findViewById(R.id.event_date)
        var serviceDate: TextView = view.findViewById(R.id.service_date)
        var unlikeButton: ImageView = view.findViewById(R.id.unlike_imageView)
        var likeButton: ImageView = view.findViewById(R.id.like_imageView)
        var rightArrowButton: ImageView = view.findViewById(R.id.right_arrow_imageView)
        var cutoffMonthText: TextView = view.findViewById(R.id.cutoff_month_text)
        var progressBar: ProgressBar = view.findViewById(R.id.progress_bar)
        var progressDateNumber: TextView = view.findViewById(R.id.progress_date_number)


        @SuppressLint("SetTextI18n")
        fun onBind(actionDetails: ActionDetails) {
            if (actionDetails.costingType == "Bidding") {
                if (actionDetails.latestBidValue.isNullOrEmpty()){
                    amount.text = "$"
                }else{
                    amount.text = "$${actionDetails.latestBidValue}"
                }
            }
            eventName.text = actionDetails.eventName
            eventType.text = "${actionDetails.branchName} - ${actionDetails.serviceName}"
            code.text = actionDetails.eventServiceDescriptionId.toString()
            progressBar.progress = actionDetails.timeLeft.toInt()
            eventDate.text = dateFormat(actionDetails.eventDate)
            serviceDate.text = dateFormat(actionDetails.serviceDate)
            cutoffMonthText.text = dateFormat(actionDetails.serviceDate).substring(3, 6)
            progressDateNumber.text = dateFormat(actionDetails.biddingCutOffDate).substring(0, 2)
        }

        fun details(position: ActionDetails, holder: ActionDetailsViewHolder) {

            holder.likeButton.setOnClickListener {

                var bidRequestId: Int = position.bidRequestId
                var costingType: String = position.costingType
                var bidStatus: String = position.bidStatus
                var cost: String? = position.cost
                var latestBidValue: String? = position.latestBidValue
                var branchName: String = position.branchName

                val sharedPreferences =
                    context.applicationContext.getSharedPreferences("MyUser", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("bidRequestId", bidRequestId)
                editor.apply()




                if (costingType != "Bidding") {
                    callBackInterface?.callBack(
                        "Bidding",
                        bidRequestId,
                        costingType,
                        bidStatus,
                        cost,
                        latestBidValue,
                        branchName
                    )
                } else {
                    com.smf.events.ui.quotedetailsdialog.QuoteDetailsDialog.newInstance(
                        bidRequestId,
                        costingType,
                        bidStatus,
                        cost,
                        latestBidValue,
                        branchName
                    )
                        .show(
                            (context as androidx.fragment.app.FragmentActivity).supportFragmentManager,
                            com.smf.events.ui.quotedetailsdialog.QuoteDetailsDialog.TAG
                        )
                }
            }

        }
    }

    // Method For Date And Month Arrangement To Display UI
    private fun dateFormat(input: String): String {
        var monthCount = input.substring(0, 2)
        val date = input.substring(3, 5)
        if (monthCount[0].digitToInt() == 0) {
            monthCount = monthCount[1].toString()
        }
        val month = Month.of(monthCount.toInt()).toString().substring(0, 3)
        return "$date $month"
    }

    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBack Interface
    interface CallBackInterface {
        fun callBack(
            status: String,
            bidRequestId: Int,
            costingType: String,
            bidStatus: String,
            cost: String?,
            latestBidValue: String?,
            branchName: String
        )
    }

}