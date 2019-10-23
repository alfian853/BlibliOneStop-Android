package com.gdn.android.onestop.group.injection

import com.gdn.android.onestop.group.fragment.GroupChatFragment
import com.gdn.android.onestop.group.fragment.GroupCreateFragment
import com.gdn.android.onestop.group.fragment.GroupFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class GroupBuilderModule {

    @GroupScope
    @ContributesAndroidInjector(modules = [GroupBindModule::class, GroupProvideModule::class])
    abstract fun contributeGroupFragment() : GroupFragment

    @GroupScope
    @ContributesAndroidInjector(modules = [GroupBindModule::class, GroupProvideModule::class])
    abstract fun contributeCreateGroupFragment() : GroupCreateFragment

    @GroupScope
    @ContributesAndroidInjector(modules = [GroupBindModule::class, GroupProvideModule::class])
    abstract fun contributeGroupChatFragment() : GroupChatFragment
}