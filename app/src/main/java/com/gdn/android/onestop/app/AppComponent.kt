package com.gdn.android.onestop.app

import android.app.Application
import android.content.Context
import com.gdn.android.onestop.OneStopApplication
import com.gdn.android.onestop.group.injection.GroupBuilderModule
import com.gdn.android.onestop.idea.injection.IdeaBuilderModule
import com.gdn.android.onestop.login.injection.LoginBuilderModule
import com.gdn.android.onestop.util.SessionManager
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        MainActivityBuilderModule::class,
        IdeaBuilderModule::class,
        GroupBuilderModule::class,
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