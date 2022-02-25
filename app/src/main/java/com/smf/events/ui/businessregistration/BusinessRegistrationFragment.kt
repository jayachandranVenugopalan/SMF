package com.smf.events.ui.businessregistration

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentBusinessRegistrationBinding
import com.smf.events.databinding.FragmentDashBoardBinding
import com.smf.events.ui.dashboard.DashBoardViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class BusinessRegistrationFragment : BaseFragment<FragmentBusinessRegistrationBinding, BusinessRegistrationViewModel>(){

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): BusinessRegistrationViewModel =
        ViewModelProvider(this, factory).get(BusinessRegistrationViewModel::class.java)

    override fun getBindingVariable(): Int = BR.businessRegistrationViewModel

    override fun getContentView(): Int = R.layout.fragment_business_registration

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }



}