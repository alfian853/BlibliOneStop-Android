package com.gdn.android.onestop.idea

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.gdn.android.onestop.OneStopApplication
import com.gdn.android.onestop.base.BaseResponse
import com.gdn.android.onestop.idea.data.*
import com.gdn.android.onestop.util.NetworkUtil
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class IdeaViewModel @Inject constructor(
    private val ideaRepository: IdeaRepository,
    private val ideaClient: IdeaClient
) : ViewModel() {

    private var ideaLiveData : LiveData<PagedList<IdeaPost>> = ideaRepository.getIdeaData()

    lateinit var context: Context

    companion object {
        private const val TAG : String = "ideaViewModel"
    }

    fun getIdeaLiveData(): LiveData<PagedList<IdeaPost>> {
        if(::context.isInitialized && NetworkUtil.isConnectedToNetwork(context)){
            ideaRepository.reloadData().subscribeOn(Schedulers.io()).subscribe()
        }
        return this.ideaLiveData
    }

    fun getPostAt(postIndex : Int) : IdeaPost? {
        return ideaLiveData.value?.get(postIndex)
    }

    fun voteIdeaPost(ideaPost: IdeaPost, isVoteUp : Boolean) : Single<Boolean> {
        return Single.create{
            ideaClient.voteIdea(ideaPost.id, isVoteUp)
                .enqueue(object : Callback<BaseResponse<Boolean>>{
                    override fun onFailure(call: Call<BaseResponse<Boolean>>, t: Throwable) {
                        it.onSuccess(false)
                    }

                    override fun onResponse(
                        call: Call<BaseResponse<Boolean>>,
                        response: Response<BaseResponse<Boolean>>
                    ) {
                        if(isVoteUp){
                            if(ideaPost.isMeVoteUp){
                                ideaPost.upVoteCount--
                                ideaPost.isMeVoteUp = false
                            }
                            else{
                                ideaPost.upVoteCount++
                                ideaPost.isMeVoteUp = true
                                if(ideaPost.isMeVoteDown){
                                    ideaPost.downVoteCount--
                                    ideaPost.isMeVoteDown = false
                                }
                            }
                        }
                        else{
                            if(ideaPost.isMeVoteDown){
                                ideaPost.downVoteCount--
                                ideaPost.isMeVoteDown = false
                            }
                            else{
                                ideaPost.downVoteCount++
                                if(ideaPost.isMeVoteUp){
                                    ideaPost.upVoteCount--
                                    ideaPost.isMeVoteUp = false
                                }
                                ideaPost.isMeVoteDown = true
                            }
                        }

                        ideaRepository.save(ideaPost)

                        it.onSuccess(true)
                    }

                })
        }
    }

    fun reloadData() : Single<Boolean> {
        if(::context.isInitialized && NetworkUtil.isConnectedToNetwork(context)){
            return ideaRepository.reloadData()
        }
        return Single.create { it.onSuccess(false) }
    }


}
