package com.gdn.android.onestop.login.injection

import com.gdn.android.onestop.base.AppComponent
import com.gdn.android.onestop.login.LoginActivity
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

@LoginScope
@Component(
    modules = [AndroidInjectionModule::class, LoginBindModule::class, LoginProvideModule::class],
    dependencies = [AppComponent::class]
)
interface LoginComponent : AndroidInjector<LoginActivity> {

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent
        ) : LoginComponent
    }

}