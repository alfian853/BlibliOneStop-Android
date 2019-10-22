package com.gdn.android.onestop.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.gdn.android.onestop.R
import dagger.android.support.DaggerDialogFragment

//todo bikin fragment dialog fullscreen walau diattach didalam tag <fragment/>
abstract class BaseDialogFragment<T : ViewDataBinding> : DaggerDialogFragment(){
    lateinit var databinding : T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
    }
}