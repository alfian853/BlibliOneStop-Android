package com.gdn.android.onestop.login.injection

import com.gdn.android.onestop.login.data.AuthClient
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class LoginProvideModule {


    @LoginScope
    @Provides
    fun provideLogin(retrofit: Retrofit): AuthClient {
        return retrofit.create(AuthClient::class.java)
    }

}