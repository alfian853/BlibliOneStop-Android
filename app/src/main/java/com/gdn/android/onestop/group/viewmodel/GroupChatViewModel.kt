package com.gdn.android.onestop.group.viewmodel

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


    fun getChatLiveData() : LiveData<List<GroupChat>> =
        MediatorLiveData<List<GroupChat>>().apply {
            val realData = groupChatRepository.getChatLiveData(activeGroup!!.id)
            this.addSource(realData) {
                this.value = it.plus(pendingMsgList)
            }
            this.addSource(pendingMessage) {
                this.value = realData.value!!.plus(it)
            }

        }

    var activeGroup : Group? = null
    set(value){
        field = value
        value?.id?.let {
            viewModelScope.launch {
                val groupInfo = groupDao.getGroupInfo(it)
                if(groupInfo.isNeverFetched()){
                    groupChatRepository.loadMoreChatBefore(it)
                }
                else{
                    groupChatRepository.loadMoreChatAfter(it)
                }
            }
        }
    }
    private var chat : ChatSendRequest = ChatSendRequest()

    var chatText = ""
    @Bindable
    get(){return field}
    set(value) {
        chat.text = value
        field = value
        notifyPropertyChanged(BR.chatText)
    }

    fun loadMoreChatBefore(){
        viewModelScope.launch {
            groupChatRepository.loadMoreChatBefore(activeGroup!!.id)
        }
    }

    fun loadMoreChatAfter(){
        if(!ChatSocketClient.isConnected()){
            viewModelScope.launch {
                groupChatRepository.loadMoreChatAfter(activeGroup!!.id)
            }
        }
    }

    fun setOnReplyChat(repliedChatId : String, repliedSummary : String){
        chat.repliedId = repliedChatId
        chat.repliedText = repliedSummary
    }

    fun setOffReplyChat(){
        chat.repliedId = null
        chat.repliedText = null
    }

    private fun convertRequestChatToGroupChat(request: ChatSendRequest) : GroupChat{
        return GroupChat().apply {
            this.isMe = true
            this.text = request.text
            this.isSending = true
            this.createdAt = Long.MAX_VALUE
        }
    }

    fun sendChat(){
        viewModelScope.launch {
            val requestChat = chat
            chat = ChatSendRequest()
            chatText = ""
            pendingMsgList.add(convertRequestChatToGroupChat(requestChat))
            pendingMessage.postValue(pendingMsgList)

            groupChatRepository.sendChat(activeGroup!!.id, requestChat)

            pendingMsgList.pop()
            pendingMessage.postValue(pendingMsgList)
        }
    }



}