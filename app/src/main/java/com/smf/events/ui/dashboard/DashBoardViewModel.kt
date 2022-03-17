package com.smf.events.ui.dashboard

import android.R
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.liveData
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.smf.events.SMFApp
import com.smf.events.base.BaseViewModel
import com.smf.events.databinding.FragmentDashBoardBinding
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


class DashBoardViewModel @Inject constructor(
    private val dashBoardRepository: DashBoardRepository,
    application: Application,
) : BaseViewModel(application), AdapterView.OnItemSelectedListener {

    // EventType Api
    fun get184Types(idToken: String) = liveData(Dispatchers.IO) {
        emit(dashBoardRepository.get184Types(idToken))
    }

//    fun getServicesBranches(idToken: String,spRegId:String,serviceCategoryId:Int) = liveData(Dispatchers.IO) {
//        Log.d("TAG", "setUserDetails: $idToken,spRegId,serviceCategoryId")
//        emit(dashBoardRepository.getServicesBranches(idToken,spRegId,serviceCategoryId))
//    }

    @SuppressLint("ResourceType")
    fun allServices(mDataBinding: FragmentDashBoardBinding?, resources: ArrayList<String>) {

        val spin = mDataBinding!!.spnAllServices

        spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long,
            ) {
                callBackInterface?.itemClick(position)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val ad: ArrayAdapter<String> =
            ArrayAdapter<String>(getApplication(), R.layout.simple_spinner_item, resources)
        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        spin.adapter = ad


    }

    @SuppressLint("ResourceType")
    fun branches(
        mDataBinding: FragmentDashBoardBinding?,
        resources: ArrayList<String>,
    ) {
        //  resources1=resources as ArrayList

        var spin = mDataBinding!!.spnBranches

        spin.onItemSelectedListener = this

        var ad: ArrayAdapter<*> =
            ArrayAdapter<Any?>(getApplication(), android.R.layout.simple_spinner_item,
                resources as List<Any?>)
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ad.notifyDataSetChanged()
        mDataBinding?.spnBranches?.adapter = ad
    }

    // Fetch tokens
    fun fetchSession() {
        Amplify.Auth.fetchAuthSession(
            {
                val session = it as AWSCognitoAuthSession
                var idToken =
                    AuthSessionResult.success(session.userPoolTokens.value!!.idToken).value
                Log.d("AuthQuickStart", "Dashboard view model fetch token $idToken")
                setToken(idToken!!)
                callBackInterface?.callBack("Bearer $idToken")
            },
            { Log.e("AuthQuickStart", "Failed to fetch session", it) }
        )
    }

    // Method for save IdToken
    private fun setToken(token: String) {
        var sharedPreferences =
            getApplication<SMFApp>().getSharedPreferences("MyUser", Context.MODE_PRIVATE)
        var editor = sharedPreferences?.edit()
        editor?.putString("IdToken", token)
        editor?.apply()
    }

    private var callBackInterface: CallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: CallBackInterface) {
        callBackInterface = callback
    }

    // CallBack Interface
    interface CallBackInterface {
        fun callBack(token: String)
        fun itemClick(msg: Int)
        fun branchItemClick(branchPos: Int)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position1: Int, p3: Long) {

        //  var branchId=resources1[position].bramchId

        callBackInterface?.branchItemClick(position1)

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    // Method For Getting Service Counts
    fun getServiceCount(idToken: String, spRegId: Int) = liveData(Dispatchers.IO) {
        emit(dashBoardRepository.getServiceCount(idToken, spRegId))
    }

    // Method For Getting All Service
    fun getAllServices(idToken: String, spRegId: Int) = liveData(Dispatchers.IO) {
        emit(dashBoardRepository.getAllServices(idToken, spRegId))
    }

    // Method For Getting Branches
    fun getServicesBranches(idToken: String, spRegId: Int, serviceCategoryId: Int) =
        liveData(Dispatchers.IO) {
            emit(dashBoardRepository.getServicesBranches(idToken, spRegId, serviceCategoryId))
        }

    // Method For Getting ActionANd Status
    fun getActionAndStatus(
        idToken: String,
        spRegId: Int,
        serviceCategoryId: Int,
        serviceVendorOnboardingId: Int,
    ) = liveData(Dispatchers.IO) {
        emit(dashBoardRepository.getActionAndStatus(idToken,
            spRegId,
            serviceCategoryId,
            serviceVendorOnboardingId))
    }
}