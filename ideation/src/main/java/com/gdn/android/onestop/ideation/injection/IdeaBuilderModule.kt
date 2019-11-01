package com.gdn.android.onestop.ideation.injection

import com.gdn.android.onestop.ideation.fragment.IdeaChannelFragment
import com.gdn.android.onestop.ideation.fragment.IdeaCreateFragment
import com.gdn.android.onestop.ideation.fragment.IdeaDetailFragment
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

    @IdeaScope
    @ContributesAndroidInjector(modules = [IdeaBindModule::class, IdeaProvideModule::class])
    abstract fun contributeIdeaCreateFragment() : IdeaCreateFragment
}