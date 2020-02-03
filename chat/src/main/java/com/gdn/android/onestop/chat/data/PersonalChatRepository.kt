package com.gdn.android.onestop.chat.data

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
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
      val personalInfo = getPersonalInfo(personalChat.from)
      personalInfo.apply{
        unreadChat += 1
      }
      Log.d("saveing",ObjectMapper().writeValueAsString(personalInfo))
      chatDao.insertPersonalInfo(personalInfo)
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
      addAndProcessPersonalChat(personalChat)
    }

    return response.isSuccessful
  }

  suspend fun removeChat(username: String){
    chatDao.deletePersonalChat(username)
    chatDao.deletePersonalInfo(username)
  }

  suspend fun nukePersonalDatabase(){
    chatDao.deleteAllPersonalChat()
    chatDao.deleteAllPersonalInfo()
  }
}