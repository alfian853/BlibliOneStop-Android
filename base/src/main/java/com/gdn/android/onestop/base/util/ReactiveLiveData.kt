package com.gdn.android.onestop.base.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class ReactiveLiveData<T : LiveData<U>, U> (private var observable : T) {

    private var observer : Observer<U>? = null
    private var lifecycleOwner : LifecycleOwner? = null

    fun observe(lifecycleOwner: LifecycleOwner, observer : Observer<U>){
        observable.observe(lifecycleOwner, observer)
        this.observer = observer
    }

    fun setObservable(observable : T){
        observer?.let {
            observable.removeObserver(it)
        }

        this.observable = observable

        lifecycleOwner?.let {
            observable.observe(it, observer!!)
        }
    }

}