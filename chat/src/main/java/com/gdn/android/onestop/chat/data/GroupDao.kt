package com.gdn.android.onestop.chat.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(vararg group: Group)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(groupList: List<Group>)

    @Transaction
    suspend fun insertGroups(vararg groupList: List<Group>){
        groupList.forEach {
            insertGroup(it)
        }
    }

    @Query("select * from `Group` where id = :groupId")
    suspend fun getGroupById(groupId: String): Group

    @Query("select * from `Group`")
    suspend fun getAllGroup(): List<Group>

    @Query("select * from `Group` where type = :groupType order by name")
    fun getGroupByType(groupType: Int): LiveData<List<Group>>

    @Query("delete from `Group`")
    suspend fun deleteAllGroup()

    @Query("delete from `Group` where id = :groupId")
    fun deleteGroupById(groupId: String)

    @Query("update GroupInfo set unreadChat = 0 where id = :groupId")
    suspend fun resetUnreadedGroupChat(groupId: String)

    @Query("delete from GroupInfo where id = :groupId")
    suspend fun deleteGroupInfoById(groupId: String)

    @Transaction
    suspend fun deleteGroupData(groupId: String){
        deleteGroupById(groupId)
        deleteGroupInfoById(groupId)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun _insertGroupInfo(groupInfo: GroupInfo)

    @Transaction
    suspend fun insertGroupInfo(groupInfo: GroupInfo){
        _insertGroupInfo(groupInfo)
        val group: Group = getGroupById(groupInfo.id)
        group.unreadChat = groupInfo.unreadChat
        insertGroup(group)
    }

    @Query("select * from GroupInfo where id = :groupId")
    suspend fun _getGroupInfo(groupId: String): GroupInfo?

    @Transaction
    suspend fun getGroupInfo(groupId: String): GroupInfo {
        var groupInfo: GroupInfo? = _getGroupInfo(groupId)
        if(groupInfo == null){
            groupInfo = GroupInfo()
            groupInfo.id = groupId
            _insertGroupInfo(groupInfo)
        }
        return groupInfo
    }

    @Query("select * from GroupMeeting where meetingDate >= :currentTime order by meetingDate asc")
    fun getAllNextMeeting(currentTime: Long): LiveData<List<GroupMeeting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMeeting(meetingList: List<GroupMeeting>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMeeting(meeting: GroupMeeting)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeetingNote(vararg meetingNote: MeetingNote)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeetingNote(meetingNote: List<MeetingNote>)

    @Query("select * from MeetingNote where groupId = :groupId order by meetingNumber desc")
    fun getMeetingNoteLiveData(groupId: String): LiveData<List<MeetingNote>>

    @Query("select * from MeetingNote where id = :noteId limit 1")
    suspend fun getMeetingNoteById(noteId: String): MeetingNote

    @Query("select * from MeetingNote where groupId = :groupId and meetingNumber = :meetingNumber limit 1")
    suspend fun getMeetingNote(groupId: String, meetingNumber: Int): MeetingNote


}