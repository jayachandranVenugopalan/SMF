package com.smf.events.ui.bidrejectiondialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseDialogFragment
import com.smf.events.databinding.FragmentBidRejectionDialogBinding
import com.smf.events.ui.quotedetailsdialog.QuoteDetailsDialog
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class BidRejectionDialogFragment : BaseDialogFragment<FragmentBidRejectionDialogBinding,BidRejectionDialogViewModel>() ,BidRejectionDialogViewModel.CallBackInterface{
    @Inject
    lateinit var factory: ViewModelProvider.Factory


    companion object {
        const val TAG = "CustomDialogFragment"

        //take the title and subtitle form the Activity
        fun newInstance(
        ): BidRejectionDialogFragment {
            val bidRejectedfragment = BidRejectionDialogFragment()

            return bidRejectedfragment
        }

    }

    override fun getViewModel(): BidRejectionDialogViewModel? =
        ViewModelProvider(this, factory).get(BidRejectionDialogViewModel::class.java)

    override fun getBindingVariable(): Int =BR.bidRejectionDialogViewModel

    override fun getContentView(): Int = R.layout.fragment_bid_rejection_dialog

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()

        var window: Window? = dialog?.window
        var params: WindowManager.LayoutParams = window!!.attributes
        params.width = ((resources.displayMetrics.widthPixels * 0.9).toInt())

        window.attributes = params
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel()?.setCallBackInterface(this)
        getViewModel()?.reasonForReject(mDataBinding)
    }

    override fun callBack(status: String) {

    }
}