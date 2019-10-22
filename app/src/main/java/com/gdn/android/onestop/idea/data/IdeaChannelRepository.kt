package com.gdn.android.onestop.idea.data

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import java.text.SimpleDateFormat
import java.util.*

class IdeaChannelRepository(private val ideaDao: IdeaDao, private val ideaClient: IdeaClient) {

    @SuppressLint("SimpleDateFormat")
    val simple = SimpleDateFormat("dd MMM yyyy HH:mm:ss")

    var lastPageRequest = 1
    var isFetching = false

    companion object {
        private const val ITEM_PER_PAGE = 4
        private const val TAG = "ideaRepository"
    }

    private var ideaLiveData :LiveData<PagedList<IdeaPost>>

    init {
        Log.d("idea","create channel repo")
        this.ideaLiveData = LivePagedListBuilder(
            ideaDao.getIdeaDataSourceFactory(), ITEM_PER_PAGE
        ).build()
    }

    suspend fun update(ideaPost : IdeaPost){
        ideaDao.updateIdea(ideaPost)
    }

    fun getIdeaLiveData() : LiveData<PagedList<IdeaPost>> = ideaLiveData

    suspend fun reloadIdeaChannelData() {
        lastPageRequest = 1
        this.fetchMoreData().let {
            ideaDao.deleteAllIdeaPost()
            ideaDao.insertIdea(it)
        }
    }

    suspend fun loadMoreData() {
        val ideaList = fetchMoreData()
        ideaDao.insertIdea(ideaList)
    }

    private fun mapIdeaPostResponseToModel(response : IdeaPostResponse) : IdeaPost{
        val ideaPost = IdeaPost()
        ideaPost.id = response.id
        ideaPost.commentCount = response.commentCount
        ideaPost.isMeVoteUp = response.isMeVoteUp
        ideaPost.isMeVoteDown = response.isMeVoteDown
        ideaPost.upVoteCount = response.upVoteCount
        ideaPost.downVoteCount = response.downVoteCount
        ideaPost.username = response.username
        ideaPost.content = response.content

        val calender = Calendar.getInstance()
        calender.timeInMillis = response.createdAt
        ideaPost.createdAt = simple.format(calender.time)
        return ideaPost
    }

    private suspend fun fetchMoreData() : List<IdeaPost> {
        if(isFetching){
            return emptyList()
        }
        else{
            isFetching = true
            Log.d(TAG, "page : $lastPageRequest")
            val response = ideaClient.getIdeaPosts(lastPageRequest, ITEM_PER_PAGE)
            if(response.isSuccessful && response.body()?.data != null){

                val ideaList : List<IdeaPost> = response.body()?.data!!.map(this::mapIdeaPostResponseToModel)

                ideaDao.insertIdea(ideaList)
                lastPageRequest++
                isFetching = false
                return ideaList
            }
            else{
                return emptyList()
            }
        }
    }

    suspend fun postIdea(content : String) : Boolean {
        val response = ideaClient.postIdea(IdeaPostRequest(content))

        if(response.isSuccessful){
            val ideaPost : IdeaPost = mapIdeaPostResponseToModel(response.body()!!.data!!)
            ideaDao.insertIdea(ideaPost)
            return true
        }
        else{
            return false
        }
    }


}