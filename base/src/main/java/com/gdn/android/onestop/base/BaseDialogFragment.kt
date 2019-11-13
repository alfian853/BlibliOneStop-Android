package com.gdn.android.onestop.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment<T : ViewDataBinding> : DialogFragment(){
    lateinit var databinding : T

    override fun onCreate(savedInstanceState: Bundle?) {
        doFragmentInjection()
        super.onCreate(savedInstanceState)
    }

    open fun doFragmentInjection(){

    }
}