package com.gdn.android.onestop.idea.injection

import androidx.lifecycle.ViewModel
import com.gdn.android.onestop.app.ViewModelKey
import com.gdn.android.onestop.idea.IdeaViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class IdeaBindModule {

    @IdeaScope
    @Binds
    @IntoMap
    @ViewModelKey(IdeaViewModel::class)
    abstract fun bindViewModel(ideaViewModel: IdeaViewModel): ViewModel

}