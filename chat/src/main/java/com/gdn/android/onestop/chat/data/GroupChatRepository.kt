package com.gdn.android.onestop.chat.data

import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.chat.util.ChatUtil
import kotlin.math.max
import kotlin.math.min


class GroupChatRepository constructor(
    private val chatDao: ChatDao,
    private val groupClient: ChatClient,
    private val sessionManager: SessionManager
) {


    companion object {
        private const val PAGE_SIZE = 20
    }

    private val sessionUsername = sessionManager.user!!.username

    fun getChatLiveData(groupId: String) = chatDao.getGroupChatLiveData(groupId)

    suspend fun loadMoreChatBefore(groupId : String){
        val groupInfo = chatDao.getGroupInfo(groupId)
        if(groupInfo.hasFetchFirstChat)return
        val response = groupClient.getGroupChat(groupId, groupInfo.lowerBoundTimeStamp, null,
            PAGE_SIZE
        )

        if(response.isSuccessful){
            response.body()?.data?.let {
                if(it.isNotEmpty()){

                    var minTime = groupInfo.lowerBoundTimeStamp
                    var maxTime = groupInfo.upperBoundTimeStamp

                    it.forEach {
                        maxTime = max(it.createdAt, maxTime)
                        minTime = min(it.createdAt, minTime)
                    }

                    groupInfo.lowerBoundTimeStamp = minTime
                    groupInfo.upperBoundTimeStamp = maxTime

                    chatDao.insertGroupInfo(groupInfo)
                    val chatList : List<GroupChat> = it.map {
                        ChatUtil.mapChatResponse(it, sessionUsername).apply {
                            this.groupId = groupId
                        }
                    }
                    chatDao.insertGroupChat(chatList)
                }
                else{
                    groupInfo.hasFetchFirstChat = true
                    chatDao.insertGroupInfo(groupInfo)
                }
            }
        }
    }

    suspend fun loadMoreChatAfter(groupId: String){
        val groupInfo = chatDao.getGroupInfo(groupId)

        val response = groupClient.getGroupChat(groupId, null, groupInfo.upperBoundTimeStamp,
            PAGE_SIZE
        )

        if(response.isSuccessful){
            response.body()?.data?.let {
                if(it.isNotEmpty()){

                    var minTime = groupInfo.lowerBoundTimeStamp
                    var maxTime = groupInfo.upperBoundTimeStamp

                    it.forEach {
                        maxTime = max(it.createdAt, maxTime)
                        minTime = min(it.createdAt, minTime)
                    }

                    groupInfo.lowerBoundTimeStamp = minTime
                    groupInfo.upperBoundTimeStamp = maxTime

                    chatDao.insertGroupInfo(groupInfo)
                    val chatList : List<GroupChat> = it.map {
                        ChatUtil.mapChatResponse(it, sessionUsername).apply {
                            this.groupId = groupId
                            this.isMe = this.username == sessionUsername
                        }
                    }
                    chatDao.insertGroupChat(chatList)
                }
            }
        }
    }

    suspend fun sendChat(groupId: String, chatSendRequest: ChatSendRequest): Boolean {

        val response = groupClient.postGroupChat(groupId, chatSendRequest)

        if(response.isSuccessful){
            val groupChat = ChatUtil.mapChatResponse(response.body()!!.data!!, sessionUsername).apply {
                this.groupId = groupId
            }
            chatDao.insertGroupChat(groupChat)
        }

        return response.isSuccessful
    }

}