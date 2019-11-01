package com.gdn.android.onestop.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseFullSceenFragment<T : ViewDataBinding> : DialogFragment(){
    lateinit var databinding : T

    override fun onCreate(savedInstanceState: Bundle?) {
        doFragmentInjection()
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    abstract fun doFragmentInjection()
}