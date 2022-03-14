package com.smf.events.ui.actiondetails

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentActionDetailsBinding
import com.smf.events.databinding.FragmentActionsAndStatusBinding
import com.smf.events.ui.actionandstatusdashboard.ActionsAndStatusFragment
import com.smf.events.ui.actionandstatusdashboard.ActionsAndStatusViewModel
import com.smf.events.ui.actionandstatusdashboard.adapter.ActionsAdapter
import com.smf.events.ui.actiondetails.adapter.ActionDetailsAdapter
import com.smf.events.ui.dashboard.model.MyEvents
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class ActionDetailsFragment :
    BaseFragment<FragmentActionDetailsBinding, ActionDetailsViewModel>() {

    private lateinit var myActionDetailsRecyclerView: RecyclerView
    lateinit var actionDetailsAdapter: ActionDetailsAdapter
    private var closeBtn: ImageView? = null


    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): ActionDetailsViewModel =
        ViewModelProvider(this, factory).get(ActionDetailsViewModel::class.java)

    override fun getBindingVariable(): Int = BR.actionDetailsViewModel

    override fun getContentView(): Int = R.layout.fragment_action_details


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeBtn = mDataBinding?.closeBtn

        //Initializing actions recyclerview
        myActionDetailsRecyclerView = mDataBinding?.actionDetailsRecyclerview!!

        //Close Button Click Listener
        clickListeners()

        //Actions Recycler view
        myActionsStatusRecycler()

        
        val listActions = getActionsList()
        actionDetailsAdapter.refreshItems(listActions)


    }

    private fun myActionsStatusRecycler() {
        actionDetailsAdapter = ActionDetailsAdapter(requireContext())
        myActionDetailsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        myActionDetailsRecyclerView.adapter = actionDetailsAdapter

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

    //Close Button ClickListener
    private fun clickListeners() {

        closeBtn?.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.action_and_status_layout, ActionsAndStatusFragment())
                .setReorderingAllowed(true)
                .commit()
        }
    }


}