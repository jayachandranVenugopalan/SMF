package com.smf.events.ui.businessregistration.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.smf.events.R
import com.smf.events.ui.addservicedialog.AddServiceDialog
import com.smf.events.ui.addservicedialog.model.Services

class AddServiceAdapter(val context: Context) :
    RecyclerView.Adapter<AddServiceAdapter.AddServiceViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AddServiceAdapter.AddServiceViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.add_services_card_view, parent, false)
        return AddServiceViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: AddServiceAdapter.AddServiceViewHolder, position: Int) {

        with(holder) {
            val isExpandable: Boolean = expand
            expandablelayout.visibility = if (isExpandable) View.VISIBLE else View.GONE

            addServiceBtn.setOnClickListener {
                expand = !expand
                notifyDataSetChanged()
            }

            addService.setOnClickListener {
                //Calling Add Services Dialog Frgment
                AddServiceDialog.newInstance(getDataModelList()).show(
                    (context as FragmentActivity).supportFragmentManager,
                    AddServiceDialog.TAG
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    inner class AddServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var addServiceBtn: ImageView = view.findViewById(R.id.add_btn)
        var addService: Button = view.findViewById(R.id.add_services_btn)
        var expand: Boolean = false
        var expandablelayout: ConstraintLayout = view.findViewById(R.id.add_service_btn_layout)

    }

    // Method for ListView Data
    private fun getDataModelList(): ArrayList<Services>{

        var dataModel = ArrayList<Services>()

        dataModel.add(Services("Venue", "gvhjsa",false))
        dataModel.add(Services("Beauty", "gvhjsa",false))
        dataModel.add(Services("Catering", "gvhjsa",false))
        dataModel.add(Services("Balloons", "gvhjsa",false))
        dataModel.add(Services("Cakes","gvhjsa", false))
        dataModel.add(Services("Snakes", "gvhjsa",false))

        return  dataModel
    }
}