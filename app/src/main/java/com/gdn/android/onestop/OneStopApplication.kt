package com.gdn.android.onestop

import android.util.Log
import com.gdn.android.onestop.app.AppComponent
import com.gdn.android.onestop.app.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class OneStopApplication : DaggerApplication(){

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        var appComponent : AppComponent = DaggerAppComponent.factory()
            .create(this)
        appComponent.inject(this)

        return appComponent
    }

}