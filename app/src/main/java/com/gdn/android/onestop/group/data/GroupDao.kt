package com.gdn.android.onestop.group.data

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
    suspend fun getGroupInfo(groupId: String) : GroupInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupChat(vararg groupChat: GroupChat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupChat(groupChat: List<GroupChat>)

    @Query("select * from GroupChat where groupId = :groupId")
    fun getGroupChatLiveData(groupId: String) : LiveData<List<GroupChat>>

}