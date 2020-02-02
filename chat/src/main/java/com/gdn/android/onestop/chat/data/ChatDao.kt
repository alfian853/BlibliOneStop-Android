package com.gdn.android.onestop.chat.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupChat(vararg groupChat: GroupChat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupChat(groupChat: List<GroupChat>)

    @Query("select * from GroupChat where groupId = :groupId order by createdAt asc")
    fun getGroupChatLiveData(groupId: String): LiveData<List<GroupChat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalChat(vararg personalChat: PersonalChat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalChat(personalChat: List<PersonalChat>)

    @Query("delete from PersonalChat where `from` = :username or `to` = :username")
    suspend fun deletePersonalChat(username: String)

    @Query("select * from PersonalChat where `from` = :username or `to` = :username order by createdAt asc")
    fun getPersonalChatLiveData(username: String): LiveData<List<PersonalChat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalInfo(vararg personalInfo: PersonalInfo)

    @Query("select * from PersonalInfo where name = :username")
    suspend fun getPersonalInfo(username: String): PersonalInfo?

    @Query("select * from PersonalInfo")
    fun getAllPersonalInfo(): LiveData<List<PersonalInfo>>

    @Query("update PersonalInfo set unreadChat = 0 where name = :username")
    suspend fun resetUnreadedPersonalChat(username: String)
}