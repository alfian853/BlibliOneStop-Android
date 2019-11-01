package com.gdn.android.onestop.base.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

val calender = Calendar.getInstance()

@SuppressLint("SimpleDateFormat")
val simple = SimpleDateFormat("dd MMM yyyy HH:mm:ss")


fun Long.toDateString(): String {
    calender.timeInMillis = this
    return simple.format(calender.time)
}