package com.gdn.android.onestop.chat.injection

import androidx.lifecycle.ViewModel
import com.gdn.android.onestop.base.ViewModelKey
import com.gdn.android.onestop.chat.viewmodel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ChatBindModule {

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey(ChatListViewModel::class)
    abstract fun bindGroupViewModel(groupViewModel: ChatListViewModel): ViewModel

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey(GroupChatViewModel::class)
    abstract fun bindGroupChatViewModel(groupChatViewModel: GroupChatViewModel): ViewModel

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey(PersonalChatViewModel::class)
    abstract fun bindPersonalChatViewModel(personalChatViewModel: PersonalChatViewModel): ViewModel

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey(MeetingNoteListViewModel::class)
    abstract fun bindMeetingNoteListViewModel(meetingNoteListViewModel: MeetingNoteListViewModel): ViewModel

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey(MeetingNoteViewModel::class)
    abstract fun bindMeetingNoteViewModel(meetingNoteViewModel: MeetingNoteViewModel): ViewModel

    @GroupScope
    @Binds
    @IntoMap
    @ViewModelKey(GroupMemberViewModel::class)
    abstract fun bindGroupMemberViewModel(groupMemberViewModel: GroupMemberViewModel): ViewModel



}