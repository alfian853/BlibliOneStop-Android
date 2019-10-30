package com.gdn.android.onestop.group

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.group.data.GroupChatResponse
import com.gdn.android.onestop.group.data.GroupClient
import com.gdn.android.onestop.group.data.GroupDao
import com.gdn.android.onestop.group.util.GroupUtil
import com.gdn.android.onestop.util.SessionManager
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
        AndroidInjection.inject(this)
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
        val chat = GroupChat()
        chat.id = data["id"]!!
        chat.groupId = data["groupId"]!!
        chat.text = data["text"]!!
        chat.username = data["username"]!!
        chat.createdAt = data["createdAt"]!!.toLong()
        chat.isMeeting = data["isMeeting"]!!.toBoolean()
        chat.meetingDate = data.getOrDefault("meetingDate",null)?.toLong()
        chat.isReply = data["isReply"]!!.toBoolean()
        chat.repliedId = data.getOrDefault("repliedId",null)
        chat.repliedText = data.getOrDefault("repliedText",null)
        chat.isMe = chat.username == username

        GlobalScope.launch {
            groupDao.insertGroupChat(chat)
        }
    }
}