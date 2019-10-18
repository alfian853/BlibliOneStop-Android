package com.gdn.android.onestop.base

import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerDialogFragment

//todo bikin fragment dialog fullscreen walau diattach didalam tag <fragment/>
abstract class BaseDialogFragment<T : ViewDataBinding> : DaggerDialogFragment(){
    lateinit var databinding : T
}