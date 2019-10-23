package com.gdn.android.onestop.group.data

import android.content.Context
import android.content.SharedPreferences
import com.gdn.android.onestop.util.SessionManager
import com.gdn.android.onestop.util.SessionObserver

class GroupUpdateManager : SessionObserver {

    val context : Context

    constructor(context: Context, sessionManager: SessionManager){
        sessionManager.addObserver(this)
        this.context = context
        preferences = context.getSharedPreferences("group_update", Context.MODE_PRIVATE)
        editor = preferences.edit()
    }
    override fun onSessionExpired() {
        clear()
    }

    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    private val LAST_UPDATE_CONST = "lastUpdate"

    fun setLastUpdate(lastUpdate: Long){
        editor.putLong(LAST_UPDATE_CONST, lastUpdate)
        editor.commit()
    }

    fun getLastUpdate() : Long = preferences.getLong(LAST_UPDATE_CONST,0)

    fun clear() {
        editor.clear()
        editor.commit()
    }

}