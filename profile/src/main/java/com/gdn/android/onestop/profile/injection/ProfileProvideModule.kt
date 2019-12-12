package com.gdn.android.onestop.profile.injection

import com.gdn.android.onestop.profile.data.ProfileClient
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class ProfileProvideModule {

  @ProfileScope
  @Provides
  fun provideProfileClient(retrofit: Retrofit): ProfileClient {
    return retrofit.create(ProfileClient::class.java)
  }
}