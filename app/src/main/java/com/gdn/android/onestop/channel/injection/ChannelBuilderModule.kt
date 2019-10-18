package com.gdn.android.onestop.channel.injection

import com.gdn.android.onestop.channel.ChannelFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChannelBuilderModule {

    @ChannelScope
    @ContributesAndroidInjector(modules = [ChannelBindModule::class, ChannelProvideModule::class])
    abstract fun contributeChannelFragment() : ChannelFragment
}