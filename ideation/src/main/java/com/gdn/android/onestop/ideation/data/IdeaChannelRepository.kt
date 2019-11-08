package com.gdn.android.onestop.ideation.data

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import java.text.SimpleDateFormat

class IdeaChannelRepository(private val ideaDao: IdeaDao, private val ideaClient: IdeaClient) {

    @SuppressLint("SimpleDateFormat")
    val simple = SimpleDateFormat("dd MMM yyyy HH:mm:ss")

    var lastPageRequest = 1
    var allFetched = false
    var isFetching = false

    companion object {
        private const val ITEM_PER_PAGE = 4
        private const val TAG = "ideaRepository"
    }

    private var ideaLiveData :LiveData<List<IdeaPost>> = ideaDao.getIdeaLiveData()

    suspend fun update(ideaPost : IdeaPost){
        ideaDao.updateIdea(ideaPost)
    }

    fun getIdeaLiveData() : LiveData<List<IdeaPost>> = ideaLiveData

    suspend fun reloadIdeaChannelData() {
        Log.d("idea","reload channel")
        lastPageRequest = 1
        allFetched = false
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
        ideaPost.createdAt = response.createdAt

        return ideaPost
    }

    private suspend fun fetchMoreData() : List<IdeaPost> {
        if(isFetching || allFetched){
            return emptyList()
        }
        else{
            isFetching = true
            Log.d(TAG, "page : $lastPageRequest")
            val response = ideaClient.getIdeaPosts(lastPageRequest, ITEM_PER_PAGE)
            isFetching = false
            if(response.isSuccessful && response.body()?.data != null){

                val ideaList : List<IdeaPost> = response.body()?.data!!.map(this::mapIdeaPostResponseToModel)

                ideaDao.insertIdea(ideaList)
                lastPageRequest++
                isFetching = false
                allFetched = ideaList.isEmpty()
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