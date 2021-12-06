package com.smf.events.ui.dashboard

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentDashBoardBinding
import com.smf.events.databinding.FragmentSignupBinding
import com.smf.events.ui.dashboard.adapter.MyEventsAdapter
import com.smf.events.ui.signup.SignUpViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class DashBoardFragment : BaseFragment<FragmentDashBoardBinding, DashBoardViewModel>() {

    lateinit var myEventsRecyclerView: RecyclerView
    lateinit var adapter: MyEventsAdapter

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): DashBoardViewModel =
        ViewModelProvider(this, factory).get(DashBoardViewModel::class.java)

    override fun getBindingVariable(): Int = BR.dashBoardViewModel

    override fun getContentView(): Int = R.layout.fragment_dash_board

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myEventsRecyclerView = mDataBinding?.eventsRecyclerView!!
        // MyEvent Recycler
        myEventsRecycler()
    }

    fun myEventsRecycler() {
        adapter = MyEventsAdapter()
        myEventsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        myEventsRecyclerView.adapter = adapter
    }
}