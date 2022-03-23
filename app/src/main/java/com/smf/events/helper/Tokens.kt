package com.smf.events.helper

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.smf.events.SMFApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*
import javax.inject.Inject


class Tokens @Inject constructor() {

    // Method for verify token validity
    fun checkTokenExpiry(application: SMFApp, caller: String, idToken: String) {

//        var newDateTime = Date().time / 1000
//        var newTime = Math.round(newDateTime.toDouble())
//        Log.d("TAG", "checkTokenExpiry refereshTokensystemtime: $newTime")
//        val splitToken = idToken.split('.');
//        val decodedBytes = Base64.getDecoder().decode(splitToken[1])
//        val decodeToken = String(decodedBytes)
//        Log.d("TAG", "checkTokenExpiry refereshToken: ${decodeToken}")
//        val tokenObj = JSONObject(decodeToken)
//        var time = tokenObj.getString("exp")
//        Log.d("TAG", "checkTokenExpiry refereshTokentime: ${time}")
//        val newTimeMin = newTime + 5 * 60;
//        if (newTimeMin > time.toDouble()) {
//            Log.d("TAG", "checkTokenExpiry: inside if loop")
        Tokens().fetchSession(application, myLambFunc, caller)
//        }else{
                Log.d("TAG", "checkTokenExpiry: outside if loop")
//        tokenNotExpired(idToken, myLambFunc, caller)
//    }

    }

    //Method for Sending Not Expired Token
    private fun tokenNotExpired(
        idToken: String,
        myFunc: suspend (String, String) -> Unit,
        caller: String
    ) {
        GlobalScope.launch {
            myFunc(idToken, caller)
        }
    }

    //Method for fetching token
    private fun fetchSession(
        application: SMFApp,
        myFunc: suspend (String, String) -> Unit,
        caller: String
    ) {

        Amplify.Auth.fetchUserAttributes({
            Log.d("AuthQuickStart", "fetch UserAttributes $it")
            Amplify.Auth.fetchAuthSession(
                {
                    val session = it as AWSCognitoAuthSession
                    val idToken =
                        AuthSessionResult.success(session.userPoolTokens.value?.idToken).value
                    Log.d("TAG", "token idToken token class: $idToken")

                    val sharedPreferences =
                        application.applicationContext.getSharedPreferences(
                            "MyUser",
                            Context.MODE_PRIVATE
                        )
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString("IdToken", idToken)
                    editor.apply()
                    GlobalScope.launch {
                        myFunc("Bearer ${sharedPreferences?.getString("IdToken", "")}", caller)
                    }
                },
                { Log.e("AuthQuickStart", "Failed to fetch session", it) })

        }, { Log.e("AuthQuickStart", "Failed to fetch UserAttributes", it) })

    }

    // Lambda Function for callBack
    private val myLambFunc: suspend (String, String) -> Unit = { token, caller ->
        idTokenCallBackInterface!!.tokenCallBack(token, caller)
    }

    private var idTokenCallBackInterface: IdTokenCallBackInterface? = null

    // Initializing CallBack Interface Method
    fun setCallBackInterface(callback: IdTokenCallBackInterface) {
        idTokenCallBackInterface = callback
    }

    // CallBack Interface
    interface IdTokenCallBackInterface {
        suspend fun tokenCallBack(idToken: String, caller: String)
    }

}