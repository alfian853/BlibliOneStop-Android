package com.gdn.android.onestop.util

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast

class DefaultContextWrapper(base: Context?) : ContextWrapper(base) {

    fun toast(message : String, duration : Int = Toast.LENGTH_SHORT){
        Toast.makeText(this, message, duration).show()
    }
}