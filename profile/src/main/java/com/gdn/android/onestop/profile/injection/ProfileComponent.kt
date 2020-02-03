package com.gdn.android.onestop.profile.injection

import com.gdn.android.onestop.ideation.injection.IdeaComponent
import com.gdn.android.onestop.profile.fragment.ProfileDialogFragment
import com.gdn.android.onestop.profile.fragment.ProfileFragment
import dagger.Component
import dagger.android.AndroidInjectionModule

@ProfileScope
@Component(
  modules = [AndroidInjectionModule::class, ProfileProvideModule::class, ProfileBindModule::class],
  dependencies = [IdeaComponent::class]
)
interface ProfileComponent {

  fun inject(profileFragment: ProfileFragment)
  fun inject(profileDialogFragment: ProfileDialogFragment)

  companion object {
    private var instance : ProfileComponent? = null

    fun getInstance() : ProfileComponent {
      var localInstance = instance
      if(localInstance == null){
        synchronized(IdeaComponent::class){
          localInstance = instance
          if(localInstance == null){
            instance = DaggerProfileComponent.factory().create(IdeaComponent.getInstance())
            localInstance =
              instance
          }
        }
      }
      return instance!!
    }
  }

  @Component.Factory
  interface Factory {
    fun create(ideaComponent: IdeaComponent) : ProfileComponent
  }


}