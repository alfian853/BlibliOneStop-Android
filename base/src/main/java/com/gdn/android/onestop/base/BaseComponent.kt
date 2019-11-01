package com.gdn.android.onestop.base

import android.app.Application
import android.content.Context
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
interface BaseComponent : AndroidInjector<Application> {

    val sessionManager : SessionManager
    val application : Application
    val context : Context
    val retrofit : Retrofit
    val networkUtil : NetworkUtil

    companion object {
        private var instance : BaseComponent? = null

        fun getInstance() : BaseComponent? {
            return instance
        }

        fun setInstance(appComponent: BaseComponent){
            instance = appComponent
        }
    }

    @Component.Factory
    interface Factory{
        fun create(
            @BindsInstance application: Application,
            @BindsInstance sessionManager: SessionManager,
            @BindsInstance context : Context
        ) : BaseComponent
    }
}