package com.gdn.android.onestop.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast

class DefaultContextWrapper(base: Context?) : ContextWrapper(base) {
//class DefaultContextWrapper(val base: Activity) : ContextWrapper(base) {

    fun toast(message : String, duration : Int = Toast.LENGTH_SHORT){
        Toast.makeText(this, message, duration).show()
    }

    fun startActivity(activityClass : Class<out Activity>){
        val intent = Intent(baseContext, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
//        base.finish()
    }
}