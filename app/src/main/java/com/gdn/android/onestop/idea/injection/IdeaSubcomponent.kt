package com.gdn.android.onestop.idea.injection

import com.gdn.android.onestop.idea.IdeaActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@IdeaScope
@Subcomponent(modules = [IdeaBindModule::class, IdeaProvideModule::class])
interface IdeaSubcomponent : AndroidInjector<IdeaActivity> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<IdeaActivity>

}