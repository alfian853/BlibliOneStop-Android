package com.gdn.android.onestop.ideation.injection

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.base.AppComponent
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.ideation.data.IdeaDao
import com.gdn.android.onestop.ideation.fragment.IdeaChannelFragment
import com.gdn.android.onestop.ideation.fragment.IdeaCreateFragment
import com.gdn.android.onestop.ideation.fragment.IdeaDetailFragment
import com.gdn.android.onestop.ideation.util.VoteHelper
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import retrofit2.Retrofit
import javax.inject.Provider

@IdeaScope
@Component(
    modules = [AndroidInjectionModule::class, IdeaBindModule::class, IdeaProvideModule::class],
    dependencies = [AppComponent::class]
)
interface IdeaComponent : AndroidInjector<Fragment> {

    val sessionManager : SessionManager
    val voteHelper: VoteHelper
    val retrofit: Retrofit
    val ideaDao: IdeaDao

    fun inject(ideaChannelFragment: IdeaChannelFragment)
    fun inject(ideaCreateFragment: IdeaCreateFragment)
    fun inject(ideaDetailFragment: IdeaDetailFragment)

    companion object {
        private var instance : IdeaComponent? = null

        fun getInstance() : IdeaComponent {
            var localInstance = instance
            if(localInstance == null){
                synchronized(IdeaComponent::class){
                    localInstance = instance
                    if(localInstance == null){
                        instance = DaggerIdeaComponent.factory().create(AppComponent.getInstance()!!)
                        localInstance = instance
                    }
                }
            }
            return instance!!
        }
    }

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent) : IdeaComponent
    }

}