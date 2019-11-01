package com.gdn.android.onestop.app

import android.app.Application
import com.gdn.android.onestop.base.BaseComponent
import com.gdn.android.onestop.base.DaggerBaseComponent
import com.gdn.android.onestop.base.util.SessionManager

class OneStopApplication : Application(){

    val appComponent : BaseComponent by lazy { DaggerBaseComponent.factory()
        .create(this, SessionManager(this), baseContext) }

    override fun onCreate() {
        appComponent.inject(this)
        BaseComponent.setInstance(appComponent)
        super.onCreate()
    }
}