package com.smf.events.ui.actiondetails

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.BR
import com.smf.events.R
import com.smf.events.base.BaseFragment
import com.smf.events.databinding.FragmentActionDetailsBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.helper.AppConstants
import com.smf.events.ui.actionandstatusdashboard.ActionsAndStatusFragment
import com.smf.events.ui.actionandstatusdashboard.model.ServiceProviderBidRequestDto
import com.smf.events.ui.actiondetails.adapter.ActionDetailsAdapter
import com.smf.events.ui.actiondetails.adapter.ActionDetailsAdapter.CallBackInterface
import com.smf.events.ui.actiondetails.model.ActionDetails
import com.smf.events.ui.dashboard.DashBoardFragmentDirections
import com.smf.events.ui.dashboard.model.MyEvents
import com.smf.events.ui.quotedetailsdialog.QuoteDetailsDialog
import com.smf.events.ui.quotedetailsdialog.model.BiddingQuotDto
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class ActionDetailsFragment :
    BaseFragment<FragmentActionDetailsBinding, ActionDetailsViewModel>(), CallBackInterface {

    private lateinit var myActionDetailsRecyclerView: RecyclerView
    lateinit var actionDetailsAdapter: ActionDetailsAdapter
    private var closeBtn: ImageView? = null
    private var myList = ArrayList<ServiceProviderBidRequestDto>()

    var serviceCategoryId: Int? = null
    var serviceVendorOnboardingId: Int? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryIdAndOnboardingIdAndMyListSetup()

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

        val listActions = getActionsDetailsList()
        actionDetailsAdapter.refreshItems(listActions)


    }

    private fun myActionsStatusRecycler() {
        actionDetailsAdapter = ActionDetailsAdapter(requireContext())
        myActionDetailsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        myActionDetailsRecyclerView.adapter = actionDetailsAdapter
        actionDetailsAdapter.setCallBackInterface(this)

    }

    private fun getActionsDetailsList(): ArrayList<ActionDetails> {
        var list = ArrayList<ActionDetails>()

        for (i in myList.indices) {
            list.add(
                ActionDetails(
                    myList[i].bidRequestId,
                    myList[i].serviceCategoryId,
                    myList[i].eventId,
                    myList[i].eventDate,
                    myList[i].eventName,
                    myList[i].serviceName,
                    myList[i].serviceDate,
                    myList[i].bidRequestedDate,
                    myList[i].biddingCutOffDate,
                    myList[i].costingType,
                    myList[i].cost,
                    myList[i].latestBidValue,
                    myList[i].bidStatus,
                    myList[i].isExistingUser,
                    myList[i].eventServiceDescriptionId,
                    myList[i].branchName,
                    myList[i].timeLeft
                )
            )
        }

        return list

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
            var args = Bundle()
            serviceCategoryId?.let { it1 -> args.putInt("serviceCategoryId", it1) }
            serviceVendorOnboardingId?.let { it1 -> args.putInt("serviceVendorOnboardingId", it1) }
            var actionAndStatusFragment = ActionsAndStatusFragment()
            actionAndStatusFragment.arguments = args

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.action_and_status_layout, actionAndStatusFragment)
                .setReorderingAllowed(true)
                .commit()
        }
    }

    override fun callBack(
        status: String,
        bidRequestId: Int,
        costingType: String,
        bidStatus: String,
        cost: String?,
        latestBidValue: String?,
        branchName: String
    ) {
        postQuoteDetails(bidRequestId, costingType, bidStatus, cost, latestBidValue, branchName)


    }

    fun postQuoteDetails(
        bidRequestId: Int,
        costingType: String,
        bidStatus: String,
        cost: String?,
        latestBidValue: String?,
        branchName: String
    ) {
        var getSharedPreferences = requireContext().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )

        var idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
        Log.d(QuoteDetailsDialog.TAG, "PostQuoteDetails: $idToken")
        var
                biddingQuote = BiddingQuotDto(
            bidRequestId,
            AppConstants.BID_SUBMITTED,
            branchName,
            "",
            cost,
            costingType,
            "USD($)",
            null,
            null,
            null,
            null,
            0
        )
        getViewModel().postQuoteDetails(idToken, bidRequestId, biddingQuote)
            .observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {

                        Log.d("TAG", "check token result: ${(apiResponse.response)}")
                        findNavController().navigate(DashBoardFragmentDirections.actionDashBoardFragmentToQuoteBriefFragment())
//                        activity.dismiss()
                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })
    }

    private fun categoryIdAndOnboardingIdAndMyListSetup() {
        val args = arguments
        serviceCategoryId = args?.getInt("serviceCategoryId")
        serviceVendorOnboardingId = args?.getInt("serviceVendorOnboardingId")
        myList = args?.getParcelableArrayList<ServiceProviderBidRequestDto>("list") as ArrayList
        Log.d("TAG", "newRequestApiCall actionDetailFragment : $myList")
        Log.d(
            "TAG",
            "newRequestApiCall actionDetailFragment serviceCategoryId : $serviceCategoryId"
        )
        Log.d(
            "TAG",
            "newRequestApiCall actionDetailFragment serviceVendorOnboardingId: $serviceVendorOnboardingId"
        )
    }

}