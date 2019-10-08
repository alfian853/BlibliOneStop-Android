package com.gdn.android.onestop.app

import android.app.Application
import com.gdn.android.onestop.OneStopApplication
import com.gdn.android.onestop.idea.injection.IdeaActivityBuilderModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        IdeaActivityBuilderModule::class,
        FactoryModule::class
    ]
)
interface AppComponent : AndroidInjector<OneStopApplication> {

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance application: Application) : AppComponent
    }
}