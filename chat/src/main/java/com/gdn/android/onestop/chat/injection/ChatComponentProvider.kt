package com.gdn.android.onestop.chat.injection

import com.gdn.android.onestop.chat.data.ChatDao
import com.gdn.android.onestop.chat.data.GroupChatRepository
import com.gdn.android.onestop.chat.data.GroupRepository
import com.gdn.android.onestop.chat.data.PersonalChatRepository
import javax.inject.Inject

class ChatComponentProvider {

    @Inject
    lateinit var chatDao: ChatDao

    @Inject
    lateinit var groupRepository: GroupRepository

    @Inject
    lateinit var groupChatRepository: GroupChatRepository

    @Inject
    lateinit var personalChatRepository: PersonalChatRepository
}