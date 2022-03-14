package com.smf.events.ui.quotebrief


import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smf.events.R
import com.smf.events.base.BaseFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class QuoteBriefFragment : BaseFragment<com.smf.events.databinding.FragmentQuoteBriefBinding,QuoteBriefViewModel>() ,QuoteBriefViewModel.CallBackInterface{
    var expand=false
    @Inject
    lateinit var factory: ViewModelProvider.Factory

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

//        //state progress two completed
//        getViewModel()?.progress2Completed(mDataBinding)
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

}