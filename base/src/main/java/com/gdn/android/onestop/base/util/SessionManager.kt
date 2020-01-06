package com.gdn.android.onestop.base.util

import android.content.Context
import android.content.SharedPreferences
import com.gdn.android.onestop.base.User
import com.google.gson.Gson
import java.util.*

open class SessionManager(private val context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = preferences.edit()

    private var observers : LinkedList<SessionObserver> = LinkedList()

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
        observers.forEach { it.onSessionExpired() }
        observers.clear()
    }

    fun addObserver(sessionObserver: SessionObserver){
        observers.push(sessionObserver)
    }

}