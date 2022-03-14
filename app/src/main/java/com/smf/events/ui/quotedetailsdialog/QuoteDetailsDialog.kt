package com.smf.events.ui.quotedetailsdialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.smf.events.R
import com.smf.events.ui.actiondetails.adapter.ActionDetailsAdapter
import com.smf.events.ui.actiondetails.adapter.ActionDetailsAdapter.CallBackInterface
import com.smf.events.ui.dashboard.DashBoardFragmentDirections
import com.smf.events.ui.emailverificationcode.EmailVerificationCodeFragmentDirections


class QuoteDetailsDialog : DialogFragment() , CallBackInterface{


    companion object {

        const val TAG = "CustomDialogFragment"

        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"

        //take the title and subtitle form the Activity
        fun newInstance(): QuoteDetailsDialog {

            val fragment = QuoteDetailsDialog()

            return fragment
        }

    }

    //creating the Dialog Fragment.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

    var view=inflater.inflate(R.layout.fragment_quote_details_dialog, container, false)
        return view }

    //tasks that need to be done after the creation of Dialog
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_ok).setOnClickListener {
            var firstCheckbox=view.findViewById<CheckBox>(R.id.quote_check_box_1)
            var secondCheckBox=view.findViewById<CheckBox>(R.id.quote_check_box_1)
            if (firstCheckbox.isChecked){
                Toast.makeText(activity, "is checked", Toast.LENGTH_SHORT).show()
                findNavController().navigate(DashBoardFragmentDirections.actionDashBoardFragmentToQuoteBriefFragment())
                dismiss()
            }

        }
    }

    override fun onStart() {
  super.onStart()

        var window: Window? =dialog?.window
        var params: WindowManager.LayoutParams= window!!.attributes
        params.width=((resources.displayMetrics.widthPixels *0.9).toInt())

        window.attributes=params
          dialog!!.window!!.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT));
    }

    override suspend fun callBack(status: String) {

    }


}