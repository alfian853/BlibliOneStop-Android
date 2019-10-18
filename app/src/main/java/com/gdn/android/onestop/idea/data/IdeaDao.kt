package com.gdn.android.onestop.idea.data

import androidx.paging.DataSource
import androidx.room.*


@Dao
interface IdeaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(vararg ideaPost : IdeaPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(ideaPosts: List<IdeaPost>)

    @Update
    suspend fun updateIdea(ideaPost: IdeaPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: IdeaComment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comments: List<IdeaComment>)

    @Query("select * from IdeaPost where id = :ideaId limit 1")
    suspend fun getPostById(ideaId : String) : IdeaPost

    @Query("select * from IdeaPost order by IdeaPost.createdAt DESC")
    fun getIdeaDataSourceFactory() : DataSource.Factory<Int,IdeaPost>

    @Query("delete from IdeaPost")
    suspend fun deleteAllIdeaPost()

    @Query("select * from IdeaComment where postId = :postId order by date ASC")
    fun getCommentsByPostId(postId : String) : DataSource.Factory<Int, IdeaComment>

    @Query("delete from IdeaComment where postId = :postId")
    suspend fun deleteAllIdeaCommentById(postId : String)
}