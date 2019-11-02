package com.gdn.android.onestop.base.util

import android.annotation.SuppressLint
import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

val calender: Calendar = Calendar.getInstance()

@SuppressLint("SimpleDateFormat")
val dateTimeFormat = SimpleDateFormat("dd MMM yyyy HH:mm")

@SuppressLint("SimpleDateFormat")
val timeFormat = SimpleDateFormat("HH:mm a")


fun Long.toDateString(): String {
    calender.timeInMillis = this
    return dateTimeFormat.format(calender.time)
}

fun Long.toTimeString(): String {
    calender.timeInMillis = this
    return timeFormat.format(calender.time)
}

fun String.toAliasName(): String {
    val len = this.length
    when(len){
        0 -> return ""
        1 -> return this[0].toString().toUpperCase()
    }

    var res = this.substring(0,2)
    for(i in 2 until len){
        if(this[i] == ' ' && i+1 < len){
            res = res.substring(0,1) + this[i+1]
            break
        }
    }

    return res.toUpperCase()
}

class Util {
    companion object {

        val colors = arrayOf(
            Color.rgb(33,150,243),
            Color.rgb(212,23,240),
            Color.rgb(255, 149, 25),
            Color.rgb(255,108,108),
            Color.rgb(129,184,76)
        )

        fun getColorFromString(string: String): Int {
            var sum = 0
            string.forEach { sum+=it.toInt() }
            return colors[sum%5]
        }
    }
}