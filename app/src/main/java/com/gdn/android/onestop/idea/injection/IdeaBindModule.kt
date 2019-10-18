package com.gdn.android.onestop.idea.injection

import androidx.lifecycle.ViewModel
import com.gdn.android.onestop.app.ViewModelKey
import com.gdn.android.onestop.idea.IdeaDetailViewModel
import com.gdn.android.onestop.idea.IdeaChannelViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class IdeaBindModule {

    @IdeaScope
    @Binds
    @IntoMap
    @ViewModelKey(IdeaChannelViewModel::class)
    abstract fun bindIdeaChannelViewModel(ideaChannelViewModel: IdeaChannelViewModel): ViewModel

    @IdeaScope
    @Binds
    @IntoMap
    @ViewModelKey(IdeaDetailViewModel::class)
    abstract fun bindIdeaDetailViewModel(ideaDetailViewModel: IdeaDetailViewModel): ViewModel

}