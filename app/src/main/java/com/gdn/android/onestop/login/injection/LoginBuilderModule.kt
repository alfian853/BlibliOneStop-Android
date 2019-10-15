package com.gdn.android.onestop.login.injection

import com.gdn.android.onestop.login.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LoginBuilderModule {

    @LoginScope
    @ContributesAndroidInjector(modules = [LoginBindModule::class, LoginProvideModule::class])
    abstract fun contributeLoginActivity() : LoginActivity
}