package com.gdn.android.onestop.group.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.group.data.ChatSendRequest
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.group.data.GroupChatRepository
import com.gdn.android.onestop.group.data.GroupDao
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class GroupChatViewModel
@Inject
constructor(
  private val groupDao: GroupDao,
  private val groupChatRepository: GroupChatRepository
) : BaseViewModel(){

  private var pendingMsgList: LinkedList<GroupChat> = LinkedList()
  private var pendingMessage: MutableLiveData<List<GroupChat>> = MutableLiveData()



  private suspend fun loadData(groupId: String){
    val groupInfo = groupDao.getGroupInfo(groupId)
    if(groupInfo.isNeverFetched()){
      groupChatRepository.loadMoreChatBefore(groupId)
    }
    else{
      groupChatRepository.loadMoreChatAfter(groupId)
    }
  }

  fun resetStateAndGetLiveData(groupId : String) : LiveData<List<GroupChat>> {
    activeGroupId = groupId
    pendingMsgList = LinkedList()
    pendingMessage.postValue(pendingMsgList)
    chatText = ""
    chat = ChatSendRequest()
    launch {
      loadData(groupId)
    }
    return MediatorLiveData<List<GroupChat>>().apply {
      val concreteLiveData = groupChatRepository.getChatLiveData(groupId)
      var concreteData: List<GroupChat> = emptyList()

      this.addSource(concreteLiveData) {chatList ->
        concreteData = chatList
        this.value = chatList.plus(pendingMsgList)
      }
      this.addSource(pendingMessage) {pendingMsgLd ->
        this.value = concreteData.plus(pendingMsgLd)
      }

    }

  }

  lateinit var activeGroupId : String


  private var chat : ChatSendRequest = ChatSendRequest()

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


  fun loadMoreChatBefore(){
    launch {
      groupChatRepository.loadMoreChatBefore(activeGroupId)
    }
  }

  fun setOnReplyChat(
    repliedChatId: String,
    repliedUsername: String,
    repliedSummary: String
  ){
    chat.isReply = true
    chat.repliedUsername = repliedUsername
    chat.repliedId = repliedChatId
    chat.repliedText = repliedSummary
    replyVisibility = View.VISIBLE
  }

  fun setOffReplyChat(){
    chat.isReply = false
    chat.repliedId = null
    chat.repliedUsername = null
    chat.repliedText = null
    replyVisibility = View.GONE
  }

  private fun convertRequestChatToGroupChat(request: ChatSendRequest) : GroupChat {
    return GroupChat().apply {
      isMe = true
      text = request.text
      isSending = true
      createdAt = Long.MAX_VALUE
      isReply = request.isReply
      repliedId = request.repliedId
      repliedText = request.repliedText
      repliedUsername = request.repliedUsername

      isMeeting = request.isMeeting
      meetingDate = request.meetingDate
    }
  }

  suspend fun sendChat(): Boolean{
    if(chat.text == "")return false
    if(chatText == "" && !chat.isMeeting)return false
    val requestChat = chat
    chat = ChatSendRequest()
    chatText = ""
    pendingMsgList.add(convertRequestChatToGroupChat(requestChat))
    pendingMessage.postValue(pendingMsgList)

    replyVisibility = View.GONE
    val result = groupChatRepository.sendChat(activeGroupId, requestChat)

    pendingMsgList.pop()
    pendingMessage.postValue(pendingMsgList)
    return result
  }


  suspend fun sendMeetingSchedule(datetime: Long, description: String): Boolean {
    chat.isMeeting = true
    chat.text = description
    chat.meetingDate = datetime
    return sendChat()
  }

}