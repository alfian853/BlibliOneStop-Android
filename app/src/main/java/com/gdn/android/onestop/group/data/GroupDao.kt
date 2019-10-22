package com.gdn.android.onestop.group.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroup(vararg group: Group)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroup(groupList: List<Group>)

    @Query("select * from `group` where type = :typeCode")
    fun getGroupByType(typeCode : Int) :  LiveData<List<Group>>

}