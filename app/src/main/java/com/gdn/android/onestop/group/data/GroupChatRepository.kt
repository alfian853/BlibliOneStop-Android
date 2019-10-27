package com.gdn.android.onestop.group.data

import javax.inject.Inject


class GroupChatRepository
    @Inject constructor(
        private val groupDao: GroupDao,
        private val groupClient: GroupClient) {


    fun getChatLiveData(groupId: String) = groupDao.getGroupChatLiveData(groupId)

    suspend fun loadMoreChatBefore(groupId : String){
        val groupUser = groupDao.getGroupInfo(groupId)

        val response = groupClient.getGroupChat(groupId, groupUser.lowerBoundTimeStamp, null)
    }

    suspend fun loadMoreChatAfter(groupId: String){
        val groupUser = groupDao.getGroupInfo(groupId)

        val response = groupClient.getGroupChat(groupId, null, groupUser.upperBoundTimeStamp)
    }

}