package com.gdn.android.onestop.group

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.group.data.GroupClient
import com.gdn.android.onestop.group.data.GroupDao
import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.group.injection.GroupComponent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseChatService : FirebaseMessagingService() {

    /**
     * TODO use the same groupDao instance as the groupViewModel has
     * because viewmodel liveData onChange callback not triggered from another groupDao instance (this service groupDao instance)
    **/
    @Inject
    lateinit var groupDao: GroupDao

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var groupClient: GroupClient

    private val username : String by lazy { sessionManager.user!!.username }

    private val objectMapper = ObjectMapper()

    override fun onCreate() {
        GroupComponent.getInstance().inject(this)
        super.onCreate()
    }

    override fun onNewToken(token: String) {
        GlobalScope.launch {
            groupClient.subscribeGroupsByToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("chat-onestop-firebase",objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message.data))
        val data = message.data
        if(data["username"]!! == username)return
        val chat = GroupChat().apply {
            id = data["id"]!!
            groupId = data["groupId"]!!
            text = data["text"]!!
            username = data["username"]!!
            createdAt = data["createdAt"]!!.toLong()
            isMeeting = data["isMeeting"]!!.toBoolean()
            meetingDate = data.getOrDefault("meetingDate",null)?.toLong()
            isReply = data["isReply"]!!.toBoolean()
            repliedId = data.getOrDefault("repliedId",null)
            repliedText = data.getOrDefault("repliedText",null)
            isMe = username == username
        }

        GlobalScope.launch {
            groupDao.insertGroupChat(chat)
        }
    }
}