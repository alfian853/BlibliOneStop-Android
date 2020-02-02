package com.gdn.android.onestop.chat.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.chat.data.*
import java.util.*
import javax.inject.Inject

class PersonalChatViewModel @Inject constructor(
  private val chatDao: ChatDao,
  private val personalChatRepository: PersonalChatRepository
) : BaseViewModel(){

  private var pendingMsgList: LinkedList<PersonalChat> = LinkedList()
  private var pendingMessage: MutableLiveData<List<PersonalChat>> = MutableLiveData()

  fun resetStateAndGetLiveData(username : String) : LiveData<List<PersonalChat>> {
    activeChatUsername = username
    pendingMsgList = LinkedList()
    pendingMessage.postValue(pendingMsgList)
    chatText = ""
    chat = PersonalChatSendRequest()

    return MediatorLiveData<List<PersonalChat>>().apply {
      val concreteLiveData = personalChatRepository.getChatLiveData(username)
      var concreteData: List<PersonalChat> = emptyList()

      this.addSource(concreteLiveData) {chatList ->
        concreteData = chatList
        this.value = chatList.plus(pendingMsgList)
      }
      this.addSource(pendingMessage) {pendingMsgLd ->
        this.value = concreteData.plus(pendingMsgLd)
      }

    }

  }

  lateinit var activeChatUsername : String


  private var chat : PersonalChatSendRequest =
    PersonalChatSendRequest()

  var chatText = ""
    @Bindable
    get(){return field}
    set(value) {
      chat.text = value
      field = value
      notifyPropertyChanged(BR.chatText)
    }

  var replyVisibility = View.GONE
    @Bindable
    get(){return field}
    set(value) {
      field = value
      notifyPropertyChanged(BR.replyVisibility)
    }

  fun setOnReplyChat(
    repliedChatId: String,
    repliedSummary: String,
    repliedUsername: String
  ){
    chat.isReply = true
    chat.repliedId = repliedChatId
    chat.repliedText = repliedSummary
    chat.repliedUsername = repliedUsername
    replyVisibility = View.VISIBLE
  }

  fun setOffReplyChat(){
    chat.isReply = false
    chat.repliedId = null
    chat.repliedText = null
    chat.repliedUsername = null
    replyVisibility = View.GONE
  }

  private fun convertRequestChatToPendingPersonalChat(request: PersonalChatSendRequest) : PersonalChat {

    return PersonalChat().apply {
      isMe = true
      to = activeChatUsername
      text = request.text
      isSending = true
      createdAt = Long.MAX_VALUE
      isReply = request.isReply
      repliedId = request.repliedId
      repliedText = request.repliedText
      repliedUsername = request.repliedUsername
    }
  }

  suspend fun sendChat(): Boolean{
    if(chat.text == "")return false
    val requestChat = chat
    chat = PersonalChatSendRequest()
    chatText = ""
    pendingMsgList.add(convertRequestChatToPendingPersonalChat(requestChat))
    pendingMessage.postValue(pendingMsgList)

    replyVisibility = View.GONE
    val result = personalChatRepository.sendChat(activeChatUsername, requestChat)

    pendingMsgList.pop()
    pendingMessage.postValue(pendingMsgList)
    return result
  }

}