package com.gdn.android.onestop.group.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gdn.android.onestop.base.ObservableViewModel
import com.gdn.android.onestop.group.data.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class GroupChatViewModel
@Inject
constructor(
    private val groupDao: GroupDao,
    private val groupChatRepository: GroupChatRepository
) : ObservableViewModel(){

    private var pendingMsgList : LinkedList<GroupChat> = LinkedList()
    private var pendingMessage : MutableLiveData<List<GroupChat>> = MutableLiveData()

    private lateinit var realData : LiveData<List<GroupChat>>

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
        realData = groupChatRepository.getChatLiveData(groupId)
        chatText = ""
        chat = ChatSendRequest()
        viewModelScope.launch {
            loadData(groupId)
        }
        return MediatorLiveData<List<GroupChat>>().apply {
            var realDataSize = 0
            this.addSource(realData) {chatList ->
                realDataSize = chatList.size
                this.value = chatList.plus(pendingMsgList)
            }
            this.addSource(pendingMessage) {pendingMsgLd ->
                this.value?.let {
                    this.value = it.subList(0, realDataSize-1).plus(pendingMsgLd)
                }
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
        viewModelScope.launch {
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
        }
    }

    fun sendChat(){
        viewModelScope.launch {
            if(chatText == "")return@launch
            val requestChat = chat
            chat = ChatSendRequest()
            chatText = ""
            pendingMsgList.add(convertRequestChatToGroupChat(requestChat))
            pendingMessage.postValue(pendingMsgList)

            replyVisibility = View.GONE

            groupChatRepository.sendChat(activeGroupId, requestChat)

            pendingMsgList.pop()
            pendingMessage.postValue(pendingMsgList)
        }
    }

}