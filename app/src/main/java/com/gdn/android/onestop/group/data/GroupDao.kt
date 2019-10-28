package com.gdn.android.onestop.group.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(vararg group: Group)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(groupList: List<Group>)

    @Transaction
    suspend fun insertGroups(vararg groupList : List<Group>){
        groupList.forEach {
            insertGroup(it)
        }
    }

    @Query("select * from `Group`")
    suspend fun getAllGroup() : List<Group>

    @Query("select * from `Group` where type = :groupType")
    fun getGroupByType(groupType : Int) :  LiveData<List<Group>>

    @Query("delete from `Group`")
    suspend fun deleteAllGroup()

    @Query("delete from `Group` where id = :groupId")
    fun deleteGroupById(groupId : String)

    @Query("delete from GroupInfo where id = :groupId")
    suspend fun deleteGroupInfoById(groupId: String)

    @Query("delete from GroupChat where id = :groupId")
    suspend fun deleteGroupChatById(groupId: String)


    @Transaction
    suspend fun deleteGroupData(groupId: String){
        deleteGroupById(groupId)
        deleteGroupChatById(groupId)
        deleteGroupInfoById(groupId)
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupInfo(groupInfo: GroupInfo)

    @Query("select * from GroupInfo where id = :groupId")
    suspend fun _getGroupInfo(groupId: String) : GroupInfo?

    @Transaction
    suspend fun getGroupInfo(groupId: String): GroupInfo {
        var groupInfo : GroupInfo? = _getGroupInfo(groupId)
        if(groupInfo == null){
            groupInfo = GroupInfo()
            groupInfo.id = groupId
            insertGroupInfo(groupInfo)
            Log.d("chat-onestop","create new info")
        }
        return groupInfo
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupChat(vararg groupChat: GroupChat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupChat(groupChat: List<GroupChat>)

    @Query("select * from GroupChat where groupId = :groupId order by createdAt asc")
    fun getGroupChatLiveData(groupId: String) : LiveData<List<GroupChat>>

}