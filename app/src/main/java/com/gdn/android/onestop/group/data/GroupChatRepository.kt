package com.gdn.android.onestop.group.data

import javax.inject.Inject


class GroupChatRepository
    @Inject constructor(
        private val groupDao: GroupDao,
        private val groupClient: GroupClient) {


    suspend fun loadMoreChat(){

//        val response = groupClient.getGroupChat()
    }

}