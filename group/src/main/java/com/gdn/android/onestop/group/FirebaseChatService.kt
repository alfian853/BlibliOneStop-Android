package com.gdn.android.onestop.group

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.group.data.GroupClient
import com.gdn.android.onestop.group.data.GroupDao
import com.gdn.android.onestop.group.injection.GroupComponent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseChatService : FirebaseMessagingService() {

  @Inject
  lateinit var groupDao: GroupDao

  @Inject
  lateinit var sessionManager: SessionManager

  @Inject
  lateinit var groupClient: GroupClient

  private val username : String by lazy { sessionManager.user!!.username }

  private val objectMapper = ObjectMapper()


  companion object {
    private var hasBeenSubscribed = false
  }


  override fun onCreate() {
    GroupComponent.getInstance().inject(this)
    if(!hasBeenSubscribed){
      GlobalScope.launch {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(
          OnCompleteListener { task ->
            if (!task.isSuccessful) {
              return@OnCompleteListener
            }

            // Get new Instance ID token
            val token = task.result!!.token

            GlobalScope.launch {

              groupClient.subscribeGroupsByToken(token)
              hasBeenSubscribed = true
            }
          })
      }
    }
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
    CoroutineScope(Dispatchers.IO).launch {
      groupDao.insertGroupChat(chat)
    }
  }

  override fun onDestroy() {
    Log.d("chat-onestop-firebase","on destroy")
    super.onDestroy()
  }
}