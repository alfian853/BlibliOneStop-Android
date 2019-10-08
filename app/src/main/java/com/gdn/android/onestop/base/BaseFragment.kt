package com.gdn.android.onestop.base

import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerFragment


abstract class BaseFragment<T : ViewDataBinding> : DaggerFragment(){
    lateinit var databinding : T
}