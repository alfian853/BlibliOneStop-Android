package com.gdn.android.onestop.idea.injection

import com.gdn.android.onestop.app.MainActivity
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface IdeaSubcomponent : AndroidInjector<MainActivity> {

    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MainActivity>

}