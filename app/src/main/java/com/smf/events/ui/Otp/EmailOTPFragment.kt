package com.smf.events.ui.Otp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import com.smf.events.R
import java.text.DecimalFormat
import java.text.NumberFormat


class EmailOTPFragment : Fragment() {
var textView:TextView?=null
var otptimer:Boolean=true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_o_t_p, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       textView= view.findViewById<TextView>(R.id.otp_timer)
        // Time is in millisecond so 50sec = 50000 I have used
        // countdown Interveal is 1sec = 1000 I have used
        // Time is in millisecond so 50sec = 50000 I have used
        // countdown Interveal is 1sec = 1000 I have used
        var timer=otpTimer()


            view.findViewById<TextView>(R.id.otp_resend).setOnClickListener {
                if (!timer) {
                    otpTimer()
                } else
                    Toast.makeText(requireContext(), "OTP is already send ", Toast.LENGTH_SHORT)
                        .show()
            }

    }
fun otpTimer():Boolean{
    object : CountDownTimer(32000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            // Used for formatting digit to be in 2 digits only
            val f: NumberFormat = DecimalFormat("00")
            val hour = millisUntilFinished / 3600000 % 24
            val min = millisUntilFinished / 60000 % 60
            val sec = millisUntilFinished / 1000 % 60
            textView!!.setText( f.format(min) + ":" + f.format(
                sec))
        }

        // When the task is over it will print 00:00:00 there
        override fun onFinish() {
            textView!!.setText("00:00")
            otptimer=false
        }
    }.start()

    return otptimer
}

}