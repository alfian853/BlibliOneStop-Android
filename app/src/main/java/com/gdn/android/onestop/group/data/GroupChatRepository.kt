package com.gdn.android.onestop.group.data

import android.util.Log
import androidx.paging.LivePagedListBuilder
import com.gdn.android.onestop.group.util.GroupUtil
import com.gdn.android.onestop.util.SessionManager
import kotlin.math.max
import kotlin.math.min


class GroupChatRepository constructor(
        private val groupDao: GroupDao,
        private val groupClient: GroupClient,
        private val sessionManager: SessionManager) {


    companion object {
        private const val PAGE_SIZE = 20
    }

    private val sessionUsername = sessionManager.user!!.username

    fun getChatLiveData(groupId: String) = groupDao.getGroupChatLiveData(groupId)

    suspend fun loadMoreChatBefore(groupId : String){
        val groupInfo = groupDao.getGroupInfo(groupId)
        if(groupInfo.hasFetchFirstChat)return
        val response = groupClient.getGroupChat(groupId, groupInfo.lowerBoundTimeStamp, null, PAGE_SIZE)

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

                    groupDao.insertGroupInfo(groupInfo)
                    val chatList : List<GroupChat> = it.map {
                        GroupUtil.mapChatResponse(it, sessionUsername).apply {
                            this.groupId = groupId
                        }
                    }
                    groupDao.insertGroupChat(chatList)
                }
                else{
                    groupInfo.hasFetchFirstChat = true
                    groupDao.insertGroupInfo(groupInfo)
                }
            }
        }
    }

    suspend fun loadMoreChatAfter(groupId: String){
        val groupInfo = groupDao.getGroupInfo(groupId)

        val response = groupClient.getGroupChat(groupId, null, groupInfo.upperBoundTimeStamp, PAGE_SIZE)

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

                    groupDao.insertGroupInfo(groupInfo)
                    val chatList : List<GroupChat> = it.map {
                        GroupUtil.mapChatResponse(it, sessionUsername).apply {
                            this.groupId = groupId
                            this.isMe = this.username == sessionUsername
                        }
                    }
                    groupDao.insertGroupChat(chatList)
                }
            }
        }
    }

    suspend fun sendChat(groupId: String, chatSendRequest: ChatSendRequest): Boolean {

        val response = groupClient.postGroupChat(groupId, chatSendRequest)

        if(response.isSuccessful){
            val groupChat = GroupUtil.mapChatResponse(response.body()!!.data!!, sessionUsername).apply {
                this.groupId = groupId
            }
            groupDao.insertGroupChat(groupChat)
        }

        return response.isSuccessful
    }

}