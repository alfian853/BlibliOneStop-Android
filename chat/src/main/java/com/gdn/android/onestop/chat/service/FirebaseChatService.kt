package com.gdn.android.onestop.chat.service

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.chat.data.*
import com.gdn.android.onestop.chat.fragment.GroupChatFragment
import com.gdn.android.onestop.chat.fragment.PersonalChatFragment
import com.gdn.android.onestop.chat.injection.ChatComponent
import com.gdn.android.onestop.chat.util.ChatUtil
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
  lateinit var chatDao: ChatDao

  @Inject
  lateinit var groupDao: GroupDao

  @Inject
  lateinit var groupChatRepository: GroupChatRepository

  @Inject
  lateinit var personalChatRepository: PersonalChatRepository

  @Inject
  lateinit var sessionManager: SessionManager

  @Inject
  lateinit var groupClient: GroupClient

  @Inject
  lateinit var context: Context

  private val username : String by lazy { sessionManager.user!!.username }

  private val objectMapper = ObjectMapper()


  companion object {
    private var hasBeenSubscribed = false
  }


  override fun onCreate() {
    ChatComponent.getInstance().inject(this)
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

  private fun processGroupChat(data: Map<String,String>){
    if(data["username"]!! == username)return

    val chat = GroupChat().apply {
      id = data["id"]!!
      groupId = data["groupId"]!!
      text = data["text"]!!
      username = data["username"]!!
      createdAt = data["createdAt"]!!.toLong()
      isMeeting = data["isMeeting"]!!.toBoolean()
      meetingDate = data.getOrElse("meetingDate",{null})?.toLong()
      isReply = data["isReply"]!!.toBoolean()
      repliedId = data.getOrElse("repliedId",{null})
      repliedText = data.getOrElse("repliedText",{null})
      repliedUsername = data.getOrElse("repliedUsername", { null})
      isMe = username == this@FirebaseChatService.username
    }

    CoroutineScope(Dispatchers.IO).launch {
      groupChatRepository.addAndProcessGroupChat(chat)
      val groupInfo = groupDao.getGroupInfo(chat.groupId)
      groupInfo.unreadChat += 1
      groupDao.insertGroupInfo(groupInfo)

      val username = groupDao.getGroupById(chat.groupId)

      if(username.isMute)return@launch

      val isNotInChatRoom = GroupChatFragment.instance == null || GroupChatFragment.instance!!.group.id != username.id

      if(isNotInChatRoom) {
        ChatUtil.notifyGroupChat(context, chat.username, chat.text, username)
      }
    }

  }

  private fun processPersonalChat(data: Map<String,String>){
    val chat = PersonalChat().apply {
      id = data["id"]!!
      text = data["text"]!!
      from = data["_from"]!!
      to = data["_to"]!!
      createdAt = data["createdAt"]!!.toLong()
      isReply = data["isReply"]!!.toBoolean()
      repliedId = data.getOrElse("repliedId",{null})
      repliedText = data.getOrElse("repliedText",{null})
      repliedUsername = data.getOrElse("repliedUsername", {null})
      isMe = false
    }


    CoroutineScope(Dispatchers.IO).launch {
      val personalInfo = personalChatRepository.getPersonalInfo(chat.from)
      personalChatRepository.addAndProcessPersonalChat(chat)

      if(personalInfo.isMute)return@launch

      val isNotInChatRoom = PersonalChatFragment.instance == null || PersonalChatFragment.instance!!.personalInfo.name != chat.from

      if(isNotInChatRoom) {
        ChatUtil.notifyPersonalChat(context, chat.text, personalInfo)
      }
    }
  }

  override fun onMessageReceived(message: RemoteMessage) {
    Log.d("chat-onestop-firebase",objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message.data))
    val data = message.data

    if(data.containsKey("groupId"))processGroupChat(data)
    else processPersonalChat(data)

  }

  override fun onDestroy() {
    Log.d("chat-onestop-firebase","on destroy")
    super.onDestroy()
  }
}