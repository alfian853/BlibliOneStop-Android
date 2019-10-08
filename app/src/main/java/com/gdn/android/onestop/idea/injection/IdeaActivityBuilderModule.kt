package com.gdn.android.onestop.idea.injection

import com.gdn.android.onestop.idea.IdeaActivity
import com.gdn.android.onestop.idea.IdeaChannelFragment
import com.gdn.android.onestop.idea.IdeaDetailFragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector.Factory
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap


@Module(subcomponents = [IdeaSubcomponent::class])
abstract class IdeaActivityBuilderModule {

    @Binds
    @IntoMap
    @ClassKey(IdeaActivity::class)
    abstract fun provideIdeaActivityInjector(ideaComponentFactory: IdeaSubcomponent.Factory): Factory<*>

    @IdeaScope
    @ContributesAndroidInjector(modules = [IdeaBindModule::class, IdeaProvideModule::class])
    abstract fun contributeIdeaChannelFragment() : IdeaChannelFragment

    @IdeaScope
    @ContributesAndroidInjector(modules = [IdeaBindModule::class, IdeaProvideModule::class])
    abstract fun contributeIdeaFragment() : IdeaDetailFragment

}