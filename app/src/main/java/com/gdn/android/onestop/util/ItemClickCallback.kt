package com.gdn.android.onestop.util

interface ItemClickCallback<T> {
    fun onItemClick(item : T, position : Int)
}
