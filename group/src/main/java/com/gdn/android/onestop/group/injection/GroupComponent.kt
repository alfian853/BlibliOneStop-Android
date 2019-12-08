package com.gdn.android.onestop.group.injection

import com.gdn.android.onestop.base.AppComponent
import com.gdn.android.onestop.group.ChatReplyService
import com.gdn.android.onestop.group.FirebaseChatService
import com.gdn.android.onestop.group.fragment.*
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
interface GroupComponent {

    fun inject(groupCreateFragment: GroupCreateFragment)
    fun inject(groupChatFragment: GroupChatFragment)
    fun inject(groupSettingFragment: GroupSettingFragment)
    fun inject(groupFragment: GroupFragment)
    fun inject(meetingListFragment: MeetingListFragment)
    fun inject(meetingNoteFragment: MeetingNoteListFragment)
    fun inject(meetingNoteEditFragment: MeetingNoteFragment)
    fun inject(chatService: FirebaseChatService)
    fun inject(chatReplyService: ChatReplyService)

    companion object {
        private var instance : GroupComponent? = null

        fun getInstance() : GroupComponent {
            var localInstance = instance
            if(localInstance == null){
                synchronized(GroupComponent::class){
                    localInstance = instance
                    if(localInstance == null){
                        instance = DaggerGroupComponent.factory().create(AppComponent.getInstance()!!)
                        localInstance = instance
                    }
                }
            }
            return instance!!
        }
    }

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent) : GroupComponent
    }


}