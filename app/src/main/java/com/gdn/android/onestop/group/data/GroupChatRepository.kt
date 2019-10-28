package com.gdn.android.onestop.group.data

import com.gdn.android.onestop.group.util.GroupUtil
import com.gdn.android.onestop.util.SessionManager


class GroupChatRepository constructor(
        private val groupDao: GroupDao,
        private val groupClient: GroupClient,
        private val sessionManager: SessionManager) {


    companion object {
        private const val PAGE_SIZE = 5
    }

    val sessionUsername = sessionManager.user!!.username

    fun getChatLiveData(groupId: String) = groupDao.getGroupChatLiveData(groupId)

    suspend fun loadMoreChatBefore(groupId : String){
        val groupInfo = groupDao.getGroupInfo(groupId)
        if(groupInfo.hasFetchFirstChat)return
        val response = groupClient.getGroupChat(groupId, groupInfo.lowerBoundTimeStamp, null, PAGE_SIZE)

        if(response.isSuccessful){
            response.body()?.data?.let {
                if(it.isNotEmpty()){
                    groupInfo.lowerBoundTimeStamp = it.minBy { it.createdAt }!!.createdAt
                    groupDao.insertGroupInfo(groupInfo)
                    val chatList : List<GroupChat> = it.map {
                        GroupUtil.mapChatResponse(it).apply {
                            this.groupId = groupId
                            this.isMe = this.username == sessionUsername
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
                    groupInfo.upperBoundTimeStamp = it.maxBy { it.createdAt }!!.createdAt
                    groupDao.insertGroupInfo(groupInfo)
                    val chatList : List<GroupChat> = it.map {
                        GroupUtil.mapChatResponse(it).apply {
                            this.groupId = groupId
                            this.isMe = this.username == sessionUsername
                        }
                    }
                    groupDao.insertGroupChat(chatList)
                }
            }
        }
    }

}