package com.gdn.android.onestop.app

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.android.AndroidInjector.Factory
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module(subcomponents = [MainActivityBuilderModule.MainActivitySubcomponent::class])
abstract class MainActivityBuilderModule{

    @Binds
    @IntoMap
    @ClassKey(MainActivity::class)
    abstract fun bindAndroidInjectorFactory(factory: MainActivitySubcomponent.Factory): Factory<*>

    @Subcomponent
    interface MainActivitySubcomponent : AndroidInjector<MainActivity?> {
        @Subcomponent.Factory
        interface Factory : AndroidInjector.Factory<MainActivity?>
    }
}