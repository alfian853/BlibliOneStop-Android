package com.gdn.android.onestop.base.util

import android.annotation.SuppressLint
import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.*


val calender: Calendar = Calendar.getInstance()

@SuppressLint("SimpleDateFormat")
val dateTimeFormat = SimpleDateFormat("dd MMM yyyy HH:mm")

@SuppressLint("SimpleDateFormat")
val dateFormat = SimpleDateFormat("dd MMM yyyy")

@SuppressLint("SimpleDateFormat")
val timeFormat = SimpleDateFormat("HH:mm a")

@SuppressLint("SimpleDateFormat")
val time24Format = SimpleDateFormat("HH:mm")


fun Long.toDateTime24String(): String {
    calender.timeInMillis = this
    return dateTimeFormat.format(calender.time)
}

fun Long.toDateString(): String {
    calender.timeInMillis = this
    return dateFormat.format(calender.time)
}

fun Long.toTimeString(): String {
    calender.timeInMillis = this
    return timeFormat.format(calender.time)
}

fun Long.toTime24String(): String {
    calender.timeInMillis = this
    return time24Format.format(calender.time)
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
        val MAX_STEXT_SIZE = 38

        private val colors = arrayOf(
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

        fun shrinkText(text : String): String {
            val len = minOf(MAX_STEXT_SIZE, text.length)
            var res = text.substring(0, len)
            for (i in 0 until len){
                if(res[i] == '\n'){
                    return res.substring(0, minOf(i, MAX_STEXT_SIZE-3)).plus("...")
                }
            }

            if(text.length > MAX_STEXT_SIZE){
                return text.substring(0, MAX_STEXT_SIZE-3).plus("...")
            }
            return text
        }
    }
}