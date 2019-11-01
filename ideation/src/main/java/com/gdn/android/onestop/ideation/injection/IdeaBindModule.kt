package com.gdn.android.onestop.ideation.injection

import androidx.lifecycle.ViewModel
import com.gdn.android.onestop.base.ViewModelKey
import com.gdn.android.onestop.ideation.viewmodel.IdeaDetailViewModel
import com.gdn.android.onestop.ideation.viewmodel.IdeaChannelViewModel
import com.gdn.android.onestop.ideation.viewmodel.IdeaCreateViewModel
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

    @IdeaScope
    @Binds
    @IntoMap
    @ViewModelKey(IdeaCreateViewModel::class)
    abstract fun bindIdeaCreateViewModel(ideaCreateViewModel: IdeaCreateViewModel): ViewModel

}