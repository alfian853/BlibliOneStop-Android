package com.gdn.android.onestop.chat.data

import com.gdn.android.onestop.chat.util.ChatUtil
import java.util.*

class PersonalChatRepository(
  val chatDao: ChatDao,
  val chatClient: ChatClient
){

  fun getChatLiveData(username: String) = chatDao.getPersonalChatLiveData(username)

  suspend fun addAndProcessPersonalChat(personalChat: PersonalChat){
    chatDao.insertPersonalChat(personalChat)

    val personalInfo = if(personalChat.isMe){
      getPersonalInfo(personalChat.to).apply {
        lastChat = Date().time
      }
    }
    else{
      getPersonalInfo(personalChat.from).apply {
        unreadChat += 1
        lastChat = Date().time
      }
    }
    chatDao.insertPersonalInfo(personalInfo)

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