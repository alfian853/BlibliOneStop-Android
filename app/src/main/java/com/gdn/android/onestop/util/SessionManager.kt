package com.gdn.android.onestop.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.gdn.android.onestop.login.LoginActivity
import com.gdn.android.onestop.login.data.User
import com.google.gson.Gson

class SessionManager(private val context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preferences.edit()


    fun saveLoginSession(user: User?) {
        editor.putBoolean("isLoggedIn", true)
        val gson = Gson()
        val userJson: String = gson.toJson(user)
        editor.putString("data", userJson)
        editor.commit()
    }

    val isLoggedIn: Boolean
        get() = preferences.getBoolean("isLoggedIn", false)

    val user: User?
        get() {
            val gson = Gson()
            return gson.fromJson(preferences.getString("data", ""), User::class.java)
        }

    fun logout() {
        editor.clear()
        editor.commit()
        val intent = Intent(context, LoginActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

}