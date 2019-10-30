package com.gdn.android.onestop

import com.gdn.android.onestop.app.AppComponent
import com.gdn.android.onestop.app.DaggerAppComponent
import com.gdn.android.onestop.util.SessionManager
import com.google.firebase.FirebaseApp
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class OneStopApplication : DaggerApplication(){

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        var appComponent : AppComponent = DaggerAppComponent.factory()
            .create(this, SessionManager(this), baseContext)
        appComponent.inject(this)
        FirebaseApp.initializeApp(this)

        return appComponent
    }

}