package com.gdn.android.onestop.group.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ChatDao : GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertGroupChat(vararg groupChat: GroupChat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertGroupChat(groupChat: List<GroupChat>)

    private suspend fun mapChatToMeeting(groupChat: GroupChat): GroupMeeting {
        return GroupMeeting().apply {
            chatId = groupChat.id
            groupId = groupChat.groupId
            meetingDate = groupChat.meetingDate!!
            meetingNo = groupChat.meetingNo!!
            groupName = getGroupById(groupChat.groupId).name
        }
    }

    @Transaction
    suspend fun insertGroupChat(groupChatList: List<GroupChat>){
        _insertGroupChat(groupChatList)
        val meetingList = groupChatList.filter { it.isMeeting }.map {mapChatToMeeting(it)}
        insertGroupMeeting(meetingList)
    }

    @Transaction
    suspend fun insertGroupChat(groupChat: GroupChat){
        _insertGroupChat(groupChat)

        if(groupChat.isMeeting){
            insertGroupMeeting(mapChatToMeeting(groupChat))
        }
    }

    @Query("select * from GroupChat where groupId = :groupId order by createdAt asc")
    fun getGroupChatLiveData(groupId: String): LiveData<List<GroupChat>>

}