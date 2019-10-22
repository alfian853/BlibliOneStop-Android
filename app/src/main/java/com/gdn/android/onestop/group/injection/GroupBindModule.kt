package com.gdn.android.onestop.group.injection

import androidx.lifecycle.ViewModel
import com.gdn.android.onestop.app.ViewModelKey
import com.gdn.android.onestop.group.viewmodel.GroupViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class GroupBindModule {

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey(GroupViewModel::class)
    abstract fun bindGroupViewModel(groupViewModel: GroupViewModel) : ViewModel
}