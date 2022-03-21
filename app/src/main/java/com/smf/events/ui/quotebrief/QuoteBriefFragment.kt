package com.smf.events.ui.quotebrief


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.helper.ApisResponse
import com.smf.events.ui.quotebrief.model.QuoteBrief
import com.smf.events.ui.quotedetailsdialog.QuoteDetailsDialog
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.Disposable
import java.time.Month
import javax.inject.Inject


class QuoteBriefFragment : BaseFragment<com.smf.events.databinding.FragmentQuoteBriefBinding,QuoteBriefViewModel>() ,QuoteBriefViewModel.CallBackInterface{
    var expand=false
    @Inject
    lateinit var factory: ViewModelProvider.Factory
lateinit var actionDisposable:Disposable
 var bidRequestId:Int?=0
    override fun getViewModel(): QuoteBriefViewModel? =ViewModelProvider(this,factory).get(QuoteBriefViewModel::class.java)

    override fun getBindingVariable(): Int =BR.quoteBriefViewModel

    override fun getContentView(): Int = R.layout.fragment_quote_brief

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getViewModel()?.backButtonPressed(mDataBinding!!)

        getViewModel()?.setCallBackInterface(this)


        getViewModel()?.expandableView(mDataBinding,expand)

        var getSharedPreferences = requireContext().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )

        var bidRequestId: Int? = getSharedPreferences?.getInt("bidRequestId", 0)
        Log.d(QuoteDetailsDialog.TAG, "PostQuoteDetails2: $bidRequestId")

        var idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
        Log.d(QuoteDetailsDialog.TAG, "PostQuoteDetails1: $idToken")

getViewModel()?.getQuoteBrief(idToken,bidRequestId!!)?.observe(viewLifecycleOwner, Observer { apiResponse ->

    when (apiResponse) {
        is ApisResponse.Success -> {

            Log.d("TAG", "Quotedetails Succcess: ${(apiResponse.response)}")
            apiResponse.response.data.eventDate


            setQuoteBrief(apiResponse.response)
when (apiResponse.response.data.bidStatus){

   "BID REQUESTED"->{}
    "WON"->{
        //        //state progress two completed
     getViewModel()?.progress2Completed(mDataBinding)
        mDataBinding?.txWonReject?.text="Won"
    }
}
        }
        is ApisResponse.Error -> {
            Log.d("TAG", "check token result: ${apiResponse.exception}")
        }
        else -> {
        }
    }
})

//        //state progress three completed
//        getViewModel()?.progress3Completed(mDataBinding)
//        //state progress four completed
//        getViewModel()?.progress4Completed(mDataBinding)
        }

        override fun callBack(messages: String) {

        when(messages){

            "onBackClicked" -> {
                findNavController().navigate(
                    QuoteBriefFragmentDirections.actionQuoteBriefFragmentToDashBoardFragment())
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (!actionDisposable.isDisposed) actionDisposable.dispose()
    }

    fun setQuoteBrief(response: QuoteBrief) {
        mDataBinding?.txJobTitle?.text=response.data.eventName
        mDataBinding?.txCatering?.text="${response.data.serviceName}-${response.data.branchName}"
        mDataBinding?.txJobTitle?.text=response.data.eventName
        if (response.data.costingType=="Bidding") {
            mDataBinding?.txJobAmount?.text = "$"
        }else{
            mDataBinding?.txJobAmount?.text = "$"
        }
        mDataBinding?.txJobIdnum?.text= response.data.eventServiceDescriptionId.toString()
        mDataBinding?.txEventdateValue?.text=dateFormat(response.data.eventDate)
        mDataBinding?.txBidProposalDateValue?.text=dateFormat(response.data.bidRequestedDate)
        mDataBinding?.txCutOffDateValue?.text=dateFormat(response.data.biddingCutOffDate)
        mDataBinding?.serviceDateValue?.text=dateFormat(response.data.serviceDate)
        mDataBinding?.paymentStatusValue?.text="NA"
        mDataBinding?.servicedBy?.text="NA"
        mDataBinding?.address?.text="${response.data.serviceAddressDto.addressLine1}  " +
                "${response.data.serviceAddressDto.addressLine2}   " +
                "${response.data.serviceAddressDto.city}"
        mDataBinding?.customerRating?.text="NA"


    }
    private fun dateFormat(input: String): String {
        var monthCount = input.substring(0, 2)
        val date = input.substring(3, 5)
        val year=input.substring(6,10)
        if (monthCount[0].digitToInt() == 0) {
            monthCount = monthCount[1].toString()
        }
        val month = Month.of(monthCount.toInt()).toString().substring(0, 3)
        return "$date $month $year"
    }
}