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

    @Query("delete from `group`")
    suspend fun deleteAllGroup()

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupChat(groupChat: GroupChat)

    @Query("select * from `group` where type = :groupType")
    fun getGroupByType(groupType : Int) :  LiveData<List<Group>>

    @Query("delete from `group` where id = :groupId")
    fun deleteGroupById(groupId : String)

}