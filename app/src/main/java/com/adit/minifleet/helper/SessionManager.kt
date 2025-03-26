package com.adit.minifleet.helper

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(context: Context) {

    companion object {
        const val PREF_NAME = "simpleRunningAssistant"
        const val IS_LOGGEDIN = "isLoggedIn"
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"
        private val TAG = SessionManager::class.java.simpleName
    }

    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    fun setLogin(isLoggedIn: Boolean, email: String, password: String) {
        editor.putBoolean(IS_LOGGEDIN, isLoggedIn)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PASSWORD, password)
        editor.apply()
        Log.d(TAG, "User login session modified!")
    }

    fun isLoggedIn(): Boolean = pref.getBoolean(IS_LOGGEDIN, false)

    fun destroySession() {
        editor.clear().apply()
        Log.d(TAG, "User login session destroyed!")
    }
}