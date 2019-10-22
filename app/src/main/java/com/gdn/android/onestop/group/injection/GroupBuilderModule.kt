package com.gdn.android.onestop.group.injection

import com.gdn.android.onestop.group.fragment.CreateGroupFragment
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
    abstract fun contributeCreateGroupFragment() : CreateGroupFragment


}