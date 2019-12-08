package com.gdn.android.onestop.base

import android.app.Application
import android.content.Context
import com.gdn.android.onestop.base.activity.SplashActivity
import com.gdn.android.onestop.base.util.NetworkUtil
import com.gdn.android.onestop.base.util.SessionManager
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        FactoryModule::class
    ]
)
interface AppComponent : AndroidInjector<Application> {

    val sessionManager : SessionManager
    val application : Application
    val context : Context
    val retrofit : Retrofit
    val networkUtil : NetworkUtil

    fun inject(splashActivity: SplashActivity)

    companion object {
        private var instance : AppComponent? = null

        fun getInstance() : AppComponent? {
            return instance
        }

        fun setInstance(appComponent: AppComponent){
            instance = appComponent
        }
    }

    @Component.Factory
    interface Factory{
        fun create(
            @BindsInstance application: Application,
            @BindsInstance sessionManager: SessionManager,
            @BindsInstance context : Context
        ) : AppComponent
    }
}