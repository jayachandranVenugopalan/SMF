package com.smf.events.ui.quotedetailsdialog

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.smf.events.R
import com.smf.events.ui.dashboard.DashBoardFragmentDirections
import java.io.*

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.smf.events.base.BaseDialogFragment
import com.smf.events.databinding.FragmentQuoteDetailsDialogBinding
import com.smf.events.helper.ApisResponse
import com.smf.events.ui.quotedetailsdialog.model.BiddingQuote
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

import java.lang.Exception


class QuoteDetailsDialog(
    var bidRequestId: Int,
    var costingType: String,
    var bidStatus: String,
    var cost: String?,
    var latestBidValue: String?,
    var branchName: String,
) : BaseDialogFragment<FragmentQuoteDetailsDialogBinding, QuoteDetailsDialogViewModel>(),
    QuoteDetailsDialogViewModel.CallBackInterface {
    lateinit var biddingQuote: BiddingQuote
    lateinit var file: File
  var fileName: String?=null
     var fileSize: String?=null
     var fileContent: String?=null

    companion object {

        const val TAG = "CustomDialogFragment"

        //take the title and subtitle form the Activity
        fun newInstance(
            bidRequestId: Int,
            costingType: String,
            bidStatus: String,
            cost: String?,
            latestBidValue: String?,
            branchName: String,
        ): QuoteDetailsDialog {


            val fragment = QuoteDetailsDialog(bidRequestId,
                costingType,
                bidStatus,
                cost,
                latestBidValue,
                branchName)

            return fragment
        }

    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun getViewModel(): QuoteDetailsDialogViewModel? =
        ViewModelProvider(this, factory).get(QuoteDetailsDialogViewModel::class.java)

    override fun getBindingVariable(): Int = BR.quoteDetailsDialogViewModel

    override fun getContentView(): Int = R.layout.fragment_quote_details_dialog

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
    //tasks that need to be done after the creation of Dialog

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Quote ViewModel CallBackInterface
        Log.d(TAG, "onViewCreated: new day")
        getViewModel()?.setCallBackInterface(this)

        fileUploader()

    }

    private fun fileUploader() {

        var logoUploadActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    var fileUri: Uri = data?.getData()!!

                    var filePath = fileUri.path
                    file = File(filePath)
                    fileName = filePath?.substring(filePath.lastIndexOf("/")+1);
                    Log.d(TAG, "logoUploader: $fileUri")
                    if (!fileUri.path.isNullOrEmpty()){
                    ConvertToString(fileUri)
                    getViewModel()?.OnCLickok(mDataBinding, bidRequestId, costingType)
                    }
                }
            }

        view?.findViewById<Button>(R.id.btn_file_upload)?.setOnClickListener {
            try {

                var gallaryIntent = Intent(Intent.ACTION_GET_CONTENT);
                 gallaryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                gallaryIntent.type ="*/*"
                logoUploadActivity.launch(Intent.createChooser(gallaryIntent, "Choose a file"))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(activity, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
            }

        }

    }


    fun ConvertToString(uri: Uri) {
        var uriString = uri.toString()
        Log.d("data", "onActivityResult: uri$uriString")
        try {
            val input: InputStream? = activity?.contentResolver?.openInputStream(uri)
            var bytes = getBytes(input!!)
            Log.d("data", "onActivityResult: bytes size=" + bytes?.size)
            if (bytes?.size!! <= 181204) {
                Log.d("data",
                    "onActivityResult: Base64string=" + Base64.encodeToString(bytes,
                        Base64.DEFAULT))
                fileContent = Base64.encodeToString(bytes, Base64.DEFAULT)
                fileSize= bytes?.size.toString()
            } else {
                Toast.makeText(activity, "upload file below 100kb", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
            Log.d("error", "onActivityResult: $e")
        }
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    override fun onStart() {
        super.onStart()

        var window: Window? = dialog?.window
        var params: WindowManager.LayoutParams = window!!.attributes
        params.width = ((resources.displayMetrics.widthPixels * 0.9).toInt())

        window.attributes = params
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }

    override fun callBack(status: String) {
        if (status == "Checked") {

            PostQuoteDetails()


        }
    }


    fun PostQuoteDetails() {

        var getSharedPreferences = requireContext().applicationContext.getSharedPreferences(
            "MyUser",
            Context.MODE_PRIVATE
        )

        var idToken = "Bearer ${getSharedPreferences?.getString("IdToken", "")}"
        Log.d(TAG, "PostQuoteDetails: $idToken")
        biddingQuote = BiddingQuote(bidRequestId,
            bidStatus,
            branchName,
            mDataBinding?.etComments?.text.toString(),
            null,
            costingType,
            "USD($)",
            fileContent,
            fileName,
            fileSize,
            "QUOTE_DETAILS",
            mDataBinding?.costEstimationAmount?.text.toString().toInt())
        getViewModel()?.postQuoteDetails(idToken, bidRequestId, biddingQuote)
            ?.observe(viewLifecycleOwner, Observer { apiResponse ->

                when (apiResponse) {
                    is ApisResponse.Success -> {

                        Log.d("TAG", "Success: ${(apiResponse.response)}")
                        findNavController().navigate(DashBoardFragmentDirections.actionDashBoardFragmentToQuoteBriefFragment())
                        dismiss()
                    }
                    is ApisResponse.Error -> {
                        Log.d("TAG", "check token result: ${apiResponse.exception}")
                    }
                    else -> {
                    }
                }
            })
    }

}