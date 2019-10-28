package com.gdn.android.onestop.group.viewmodel

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.viewModelScope
import com.gdn.android.onestop.base.ObservableViewModel
import com.gdn.android.onestop.group.data.*
import com.gdn.android.onestop.util.SessionManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupChatViewModel
@Inject
constructor(
    private val groupDao: GroupDao,
    private val sessionManager: SessionManager,
    private val groupChatRepository: GroupChatRepository
) : ObservableViewModel(){

    private val chatSocketClient = ChatSocketClient.getInstance(groupDao, sessionManager, this.viewModelScope)

    fun getChatLiveData(groupId: String) = groupChatRepository.getChatLiveData(groupId)

    var activeGroup : Group? = null
    set(value){
        field = value
        value?.id?.let {
            chat.groupId = it
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

    var chatText : String = "test send"
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

    fun sendChat(){
        viewModelScope.launch {
            chat.groupId = activeGroup!!.id
            chatSocketClient.sendChat(chat)
            chat = ChatSendRequest()
            chatText = ""
        }
    }




}