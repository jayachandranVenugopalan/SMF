package com.smf.events.ui.bidrejectiondialog

import android.R
import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.smf.events.base.BaseDialogViewModel
import com.smf.events.databinding.FragmentBidRejectionDialogBinding
import com.smf.events.databinding.FragmentDashBoardBinding
import javax.inject.Inject

class BidRejectionDialogViewModel@Inject constructor(application: Application):BaseDialogViewModel(application) {





    @SuppressLint("ResourceType")
    fun reasonForReject(mDataBinding: FragmentBidRejectionDialogBinding?) {
var  resources: ArrayList<String> = ArrayList()
        resources.add(0,"Reason For Rejection")
        resources.add(1,"Active")
        resources.add(2,"Not Interested")
        resources.add(2,"Other")
        val spin = mDataBinding!!.spnReason

        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
                Log.d("TAG", "onItemSelected: ${resources[position]}")
if (resources[position]=="Other"){
callBackInterface?.callBack("Other")
}


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val ad: ArrayAdapter<String> =
            ArrayAdapter<String>(getApplication(), R.layout.simple_spinner_item, resources)
        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spin.adapter = ad
    }
    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBack Interface
    interface CallBackInterface {
        fun callBack(status: String)
    }
}