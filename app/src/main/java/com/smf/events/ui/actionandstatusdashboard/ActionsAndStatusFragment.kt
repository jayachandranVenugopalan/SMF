package com.smf.events.ui.actionandstatusdashboard

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.BR
import com.smf.events.MainActivity
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentActionsAndStatusBinding
import com.smf.events.ui.actionandstatusdashboard.adapter.ActionsAdapter
import com.smf.events.ui.actiondetails.ActionDetailsFragment
import com.smf.events.ui.dashboard.adapter.StatusAdaptor
import com.smf.events.ui.dashboard.model.MyEvents
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ActionsAndStatusFragment :
    BaseFragment<FragmentActionsAndStatusBinding, ActionsAndStatusViewModel>(), ActionsAdapter.OnActionCardClickListener {

    private lateinit var myActionRecyclerView: RecyclerView
    lateinit var actionAdapter: ActionsAdapter
    private lateinit var myStatusRecyclerView: RecyclerView
    lateinit var statusAdapter: StatusAdaptor

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): ActionsAndStatusViewModel =
        ViewModelProvider(this, factory).get(ActionsAndStatusViewModel::class.java)

    override fun getBindingVariable(): Int = BR.actionsAndStatusViewModel

    override fun getContentView(): Int = R.layout.fragment_actions_and_status


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initializing actions recyclerview
        myActionRecyclerView = mDataBinding?.actionsRecyclerview!!

        //Initializing status recyclerview
        myStatusRecyclerView = mDataBinding?.statusRecyclerview!!

        //Actions  Recycler view
        myActionsStatusRecycler()

        //Status Recycler view
        myStatusRecycler()

        val listActions = getActionsList()
        actionAdapter.refreshItems(listActions)

        val listStatus = getStatusList()
        statusAdapter.refreshItems(listStatus)
    }


    private fun myActionsStatusRecycler() {
        actionAdapter = ActionsAdapter(requireContext())
        myActionRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        myActionRecyclerView.adapter = actionAdapter
        actionAdapter.setOnClickListener(this)

    }

    private fun myStatusRecycler() {
        statusAdapter = StatusAdaptor()
        myStatusRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        myStatusRecyclerView.adapter = statusAdapter

    }

    private fun getActionsList(): ArrayList<MyEvents> {
        var list = ArrayList<MyEvents>()
        list.add(MyEvents("4", "New request"))
        list.add(MyEvents("2", "Send Quotes"))
        list.add(MyEvents("1", "Won Bid"))
        list.add(MyEvents("2", "Rejected"))
        list.add(MyEvents("1", "Draft"))

        return list

    }

    private fun getStatusList(): ArrayList<MyEvents> {
        var list = ArrayList<MyEvents>()
        list.add(MyEvents("4", "Bids Submitted"))
        list.add(MyEvents("2", "Service done"))
        list.add(MyEvents("1", "Request closed"))
        list.add(MyEvents("2", "Rejected"))
        list.add(MyEvents("1", "Draft"))

        return list

    }

    // Action Card Click Listener Interface Method
    override fun actionCardClick(myEvents: MyEvents) {

        val actionDetails = ActionDetailsFragment()

        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.action_and_status_layout, actionDetails)
            .addToBackStack(ActionsAndStatusFragment::class.java.name)
            .commit()

    }


}