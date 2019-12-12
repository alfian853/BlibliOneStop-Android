package com.gdn.android.onestop.profile.injection

import androidx.lifecycle.ViewModel
import com.gdn.android.onestop.base.ViewModelKey
import com.gdn.android.onestop.profile.viewmodel.ProfileViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ProfileBindModule {

  @ProfileScope
  @Binds
  @IntoMap
  @ViewModelKey(ProfileViewModel::class)
  abstract fun bindProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

}