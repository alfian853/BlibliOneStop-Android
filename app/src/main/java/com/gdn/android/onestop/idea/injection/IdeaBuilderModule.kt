package com.gdn.android.onestop.idea.injection

import com.gdn.android.onestop.idea.IdeaChannelFragment
import com.gdn.android.onestop.idea.IdeaDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class IdeaBuilderModule {

    @IdeaScope
    @ContributesAndroidInjector(modules = [IdeaBindModule::class, IdeaProvideModule::class])
    abstract fun contributeIdeaChannelFragment() : IdeaChannelFragment

    @IdeaScope
    @ContributesAndroidInjector(modules = [IdeaBindModule::class, IdeaProvideModule::class])
    abstract fun contributeIdeaFragment() : IdeaDetailFragment

}