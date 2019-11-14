package com.gdn.android.onestop.ideation.data

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gdn.android.onestop.base.util.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class IdeaCommentRepository @Inject constructor(
    private val ideaDao: IdeaDao,
    private val ideaClient: IdeaClient,
    private val sessionManager: SessionManager
) {

    companion object {
        private const val ITEM_PER_PAGE = 2
        private const val TAG = "ideaCommentRepository"
    }

    @SuppressLint("SimpleDateFormat")
    val simple = SimpleDateFormat("dd MMM yyyy HH:mm:ss")

    var lastPageRequest = 1
    var isFetching = false

    private lateinit var commentLiveData : LiveData<PagedList<IdeaComment>>
    private lateinit var ideaId : String

    fun setIdeaPost(ideaId: String){
        this.ideaId = ideaId
        lastPageRequest = 1
        commentLiveData = LivePagedListBuilder(
            ideaDao.getCommentsByPostId(ideaId), ITEM_PER_PAGE
        ).build()
        CoroutineScope(Dispatchers.IO).launch {
            getMoreDataByPost().apply {
                ideaDao.insertComment(this)
            }
        }
    }

    fun getCommentLiveData() : LiveData<PagedList<IdeaComment>> = commentLiveData

    suspend fun loadMoreComment() : Boolean {
        return getMoreDataByPost().apply {
            ideaDao.insertComment(this)
        }.isNotEmpty()
    }

    private suspend fun getMoreDataByPost() : List<IdeaComment> {
        Log.d(TAG,"try to fetch")
        if(isFetching){
            return emptyList()
        }
        else{
            isFetching = true
            Log.d(TAG, "page : $lastPageRequest")

            val response = ideaClient.getComment(ideaId, lastPageRequest, ITEM_PER_PAGE).data!!// ?: emptyList()
            return response
                .apply {
                    forEach {
                        val calender = Calendar.getInstance()
                        calender.timeInMillis = it.date.toLong()
                        it.date = simple.format(calender.time)
                        it.postId = ideaId
                        it.id = it.hashCode().toString()
                    }
                    if (isNotEmpty()) {
                        lastPageRequest++
                        isFetching = false
                    }
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
        ideaComment.date = simple.format(calender.time)
        ideaDao.insertComment(ideaComment)
        ideaDao.getPostById(ideaId).apply {
            this.commentCount++
        }
        return true
    }
}