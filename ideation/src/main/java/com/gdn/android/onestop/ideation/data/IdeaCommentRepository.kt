package com.gdn.android.onestop.ideation.data

import androidx.lifecycle.LiveData
import com.gdn.android.onestop.base.util.SessionManager
import java.util.*
import javax.inject.Inject

class IdeaCommentRepository @Inject constructor(
    private val ideaDao: IdeaDao,
    private val ideaClient: IdeaClient,
    private val sessionManager: SessionManager
) {

    companion object {
        private const val ITEM_PER_PAGE = 6
    }

    var lastPageRequest = 1

    private lateinit var commentLiveData : LiveData<List<IdeaComment>>
    private lateinit var ideaId : String

    fun setIdeaPost(ideaId: String){
        this.ideaId = ideaId
        lastPageRequest = 1
        commentLiveData = ideaDao.getCommentsByPostId(ideaId)
    }

    fun getCommentsLiveData() : LiveData<List<IdeaComment>> = commentLiveData

    suspend fun loadMoreComment() : Boolean {
        return getMoreDataByPost().apply {
            ideaDao.insertComment(this)
        }.isNotEmpty()
    }

    private suspend fun getMoreDataByPost() : List<IdeaComment> {

        val response = ideaClient.getComment(ideaId, lastPageRequest, ITEM_PER_PAGE).data!!// ?: emptyList()
        return response
            .apply {
                forEach {
                    val calender = Calendar.getInstance()
                    calender.timeInMillis = it.date.toLong()
                    it.postId = ideaId
                    it.id = it.hashCode().toString()
                }
                if (isNotEmpty()) {
                    lastPageRequest++
                }
            }

    }

    suspend fun submitComment(commentText : String) : Boolean {

        val response = ideaClient.postComment(ideaId, CommentPostRequest(commentText))

        if(!response.isSuccessful){
            return false
        }
        val ideaComment = response.body()?.data ?: return false
        ideaComment.username = sessionManager.user!!.username
        ideaComment.id = ideaComment.hashCode().toString()
        val calender = Calendar.getInstance()
        calender.timeInMillis = ideaComment.date.toLong()
        ideaDao.insertComment(ideaComment)
        val ideaPost = ideaDao.getPostById(ideaId).apply {
            this.commentCount++
        }
        ideaDao.updateIdea(ideaPost)
        return true
    }
}