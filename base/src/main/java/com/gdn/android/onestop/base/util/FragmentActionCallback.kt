package com.gdn.android.onestop.base.util

interface FragmentActionCallback<T> {
    fun onActionSuccess(data : T)
    fun onFailure(cause : String){}
}