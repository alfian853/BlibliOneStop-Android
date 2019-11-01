package com.gdn.android.onestop.base.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo


class NetworkUtil(val context: Context) {

    fun isConnectedToNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnected?: false

    }
}