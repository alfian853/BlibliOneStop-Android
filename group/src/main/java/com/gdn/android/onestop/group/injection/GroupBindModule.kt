package com.gdn.android.onestop.group.injection

import androidx.lifecycle.ViewModel
import com.gdn.android.onestop.base.ViewModelKey
import com.gdn.android.onestop.group.viewmodel.GroupChatViewModel
import com.gdn.android.onestop.group.viewmodel.MeetingNoteListViewModel
import com.gdn.android.onestop.group.viewmodel.GroupViewModel
import com.gdn.android.onestop.group.viewmodel.MeetingNoteViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class GroupBindModule {

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey(GroupViewModel::class)
    abstract fun bindGroupViewModel(groupViewModel: GroupViewModel): ViewModel

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey(GroupChatViewModel::class)
    abstract fun bindGroupChatViewModel(groupChatViewModel: GroupChatViewModel): ViewModel

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey((MeetingNoteListViewModel::class))
    abstract fun bindMeetingNoteListViewModel(meetingNoteListViewModel: MeetingNoteListViewModel): ViewModel

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey((MeetingNoteViewModel::class))
    abstract fun bindMeetingNoteViewModel(meetingNoteViewModel: MeetingNoteViewModel): ViewModel

}