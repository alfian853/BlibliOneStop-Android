package com.gdn.android.onestop.chat.injection

import com.gdn.android.onestop.base.AppComponent
import com.gdn.android.onestop.chat.data.ChatDao
import com.gdn.android.onestop.chat.service.ChatReplyService
import com.gdn.android.onestop.chat.service.FirebaseChatService
import com.gdn.android.onestop.chat.fragment.*
import dagger.Component
import dagger.android.AndroidInjectionModule

@GroupScope
@Component(
    modules = [
        AndroidInjectionModule::class,
        GroupBindModule::class,
        GroupProvideModule::class
    ],
    dependencies = [AppComponent::class]
)
interface ChatComponent {

    val chatDao: ChatDao

    fun inject(groupCreateFragment: GroupCreateFragment)
    fun inject(groupChatFragment: GroupChatFragment)
    fun inject(personalChatFragment: PersonalChatFragment)
    fun inject(groupSettingFragment: GroupSettingFragment)
    fun inject(groupFragment: ChatChannelFragment)
    fun inject(meetingListFragment: MeetingListFragment)
    fun inject(meetingNoteFragment: MeetingNoteListFragment)
    fun inject(meetingNoteEditFragment: MeetingNoteFragment)
    fun inject(groupMemberFragment: GroupMemberFragment)
    fun inject(chatService: FirebaseChatService)
    fun inject(chatReplyService: ChatReplyService)

    fun inject(chatComponentProvider: ChatComponentProvider)

    companion object {
        private var instance : ChatComponent? = null

        fun getInstance() : ChatComponent {
            var localInstance = instance
            if(localInstance == null){
                synchronized(ChatComponent::class){
                    localInstance = instance
                    if(localInstance == null){
                        instance = DaggerChatComponent.factory().create(AppComponent.getInstance()!!)
                        localInstance = instance
                    }
                }
            }
            return instance!!
        }
    }

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent) : ChatComponent
    }


}