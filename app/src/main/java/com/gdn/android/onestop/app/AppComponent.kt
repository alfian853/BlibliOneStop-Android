package com.gdn.android.onestop.app

import android.app.Application
import android.content.Context
import com.gdn.android.onestop.OneStopApplication
import com.gdn.android.onestop.channel.injection.ChannelBuilderModule
import com.gdn.android.onestop.idea.injection.IdeaBuilderModule
import com.gdn.android.onestop.login.injection.LoginBuilderModule
import com.gdn.android.onestop.util.SessionManager
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        MainActivityBuilderModule::class,
        IdeaBuilderModule::class,
        ChannelBuilderModule::class,
        LoginBuilderModule::class,
        FactoryModule::class
    ]
)
interface AppComponent : AndroidInjector<OneStopApplication> {

    @Component.Factory
    interface Factory{
        fun create(
            @BindsInstance application: Application,
            @BindsInstance sessionManager: SessionManager,
            @BindsInstance context : Context
        ) : AppComponent
    }
}