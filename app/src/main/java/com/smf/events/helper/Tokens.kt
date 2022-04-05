package com.smf.events.helper

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.smf.events.SMFApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import javax.inject.Inject


class Tokens @Inject constructor() {

    // Method for verify token validity
    fun checkTokenExpiry(application: SMFApp, caller: String, idToken: String) {

        val newTime = Date().time / 1000
        val splitToken = idToken.split('.')
        val decodedBytes = Base64.getDecoder().decode(splitToken[1])
        val decodeToken = String(decodedBytes)
        val tokenObj = JSONObject(decodeToken)
        val tokenObjExp = tokenObj.getString("exp").toLong()
        val newTimeMin = newTime + 1 * 60

        if (newTimeMin < tokenObjExp) {
            Log.d("TAG", "checkTokenExpiry refereshTokentime inside if block")
            tokenNotExpired(idToken, myLambFunc, caller)
        } else {
            Log.d("TAG", "checkTokenExpiry refereshTokentime else block")
            fetchNewIdToken(application, myLambFunc, caller)
        }

    }

    //Method for fetching token
    private fun fetchNewIdToken(
        application: SMFApp,
        myFunc: suspend (String, String) -> Unit,
        caller: String
    ) {
        Amplify.Auth.fetchAuthSession(
            {
                val session = it as AWSCognitoAuthSession
                val idToken =
                    AuthSessionResult.success(session.userPoolTokens.value?.idToken).value

                updateTokenToShardPreferences(application, idToken, myFunc, caller)

            },
            { Log.e("AuthQuickStart", "Failed to fetch session", it) })


    }

    // Method For Update New IdToken to Shared Preference
    private fun updateTokenToShardPreferences(
        application: SMFApp,
        idToken: String?,
        myFunc: suspend (String, String) -> Unit,
        caller: String
    ) {
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

    //Method for SignOut Current User
    private fun signOutCurrentUser(
        application: SMFApp,
        myFunc: suspend (String, String) -> Unit
    ) {
        Amplify.Auth.signOut(
            {
                Log.i(
                    "AuthQuickstart",
                    "checkTokenExpiry refereshTokentime Signed out successfully"
                )
                val sharedPreferences =
                    application.applicationContext.getSharedPreferences(
                        "MyUser",
                        Context.MODE_PRIVATE
                    )
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("IdToken", "")
                editor.apply()
                GlobalScope.launch {
                    myFunc("", "signOut")
                }

            },
            { Log.e("AuthQuickstart", "Sign out failed", it) }
        )
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