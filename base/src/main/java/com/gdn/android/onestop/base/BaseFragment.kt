package com.gdn.android.onestop.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.support.DaggerFragment


abstract class BaseFragment<T : ViewDataBinding> : Fragment(){
    lateinit var databinding : T

    override fun onCreate(savedInstanceState: Bundle?) {
        doFragmentInjection()
        super.onCreate(savedInstanceState)
    }

    open fun doFragmentInjection(){}
}