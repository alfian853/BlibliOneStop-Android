package com.gdn.android.onestop.idea.data

import androidx.paging.DataSource
import androidx.room.*


@Dao
interface IdeaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg ideaPost : IdeaPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(ideaPost: List<IdeaPost>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg comment: IdeaComment)

    @Query("select * from IdeaPost order by IdeaPost.createdAt DESC")
    fun getIdeaDataSourceFactory() : DataSource.Factory<Int,IdeaPost>

    @Query("delete from IdeaPost")
    fun deleteAll()
}