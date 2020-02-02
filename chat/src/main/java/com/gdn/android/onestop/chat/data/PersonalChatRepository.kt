package com.gdn.android.onestop.chat.data

import com.gdn.android.onestop.chat.util.ChatUtil
import javax.inject.Inject

class PersonalChatRepository @Inject constructor(
    val chatDao: ChatDao,
    val chatClient: ChatClient
){

  fun getChatLiveData(username: String) = chatDao.getPersonalChatLiveData(username)

  suspend fun addAndProcessPersonalChat(personalChat: PersonalChat){
    chatDao.insertPersonalChat(personalChat)

    if(!personalChat.isMe){
      val personalInfo = chatDao.getPersonalInfo(personalChat.from)

      if(personalInfo == null){
        chatDao.insertPersonalInfo(getPersonalInfo(personalChat.from).apply {
          unreadChat = 1
        })
      }

    }
  }

  val personalLiveData = chatDao.getAllPersonalInfo()

  suspend fun getPersonalInfo(username: String): PersonalInfo {

    return chatDao.getPersonalInfo(username) ?: PersonalInfo().apply {
      id = username
      this.name = username
    }
  }

  suspend fun sendChat(username: String, chatSendRequest: PersonalChatSendRequest): Boolean {

    val response = chatClient.postPersonalChat(username, chatSendRequest)

    if(response.isSuccessful){
      val responseBody = response.body()!!.data!!
      val personalChat = ChatUtil.mapPersonalChatResponse(responseBody, true)
      chatDao.insertPersonalChat(personalChat)
    }

    return response.isSuccessful
  }

}