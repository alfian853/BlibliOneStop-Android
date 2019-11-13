package com.gdn.android.onestop.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseFullScreenFragment<T : ViewDataBinding> : BaseDialogFragment<T>(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }
}