package com.gdn.android.onestop.ideation.util

import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import com.gdn.android.onestop.base.BaseResponse
import com.gdn.android.onestop.base.FaSolidTextView
import com.gdn.android.onestop.base.util.DefaultContextWrapper
import com.gdn.android.onestop.ideation.R
import com.gdn.android.onestop.ideation.data.IdeaChannelRepository
import com.gdn.android.onestop.ideation.data.IdeaClient
import com.gdn.android.onestop.ideation.data.IdeaPost
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VoteHelper(
    private val ideaChannelRepository: IdeaChannelRepository,
    private val ideaClient: IdeaClient) {

    private fun voteIdeaPost(ideaPost: IdeaPost, isVoteUp : Boolean) : Single<Boolean> {
        return Single.create{
            ideaClient.voteIdea(ideaPost.id, isVoteUp)
                .enqueue(object : Callback<BaseResponse<Boolean>> {
                    override fun onFailure(call: Call<BaseResponse<Boolean>>, t: Throwable) {
                        it.onSuccess(false)
                    }

                    override fun onResponse(
                        call: Call<BaseResponse<Boolean>>,
                        response: Response<BaseResponse<Boolean>>
                    ) {
                        if(!response.isSuccessful){
                            it.onSuccess(false)
                        }
                        else{
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

                        }

                        CoroutineScope(Dispatchers.IO).launch {
                            ideaChannelRepository.update(ideaPost)
                        }

                        it.onSuccess(true)
                    }

                })
        }
    }

    fun clickVote(tvUpVote : FaSolidTextView, tvDownVote : FaSolidTextView,
                  contextWrapper: DefaultContextWrapper, ideaPost: IdeaPost, isVoteUp: Boolean){

        val resources = contextWrapper.resources

        if(isVoteUp){
            clickVoteUtil(tvUpVote, resources, true, ideaPost.upVoteCount, ideaPost.isMeVoteUp)
            if(ideaPost.isMeVoteDown)
                clickVoteUtil(tvDownVote, resources,false, ideaPost.downVoteCount, true)
        }
        else{
            clickVoteUtil(tvDownVote, resources, false, ideaPost.downVoteCount, ideaPost.isMeVoteDown)
            if(ideaPost.isMeVoteUp)
                clickVoteUtil(tvUpVote, resources, true, ideaPost.upVoteCount, true)
        }

        this.voteIdeaPost(ideaPost, isVoteUp)
            .subscribeOn(Schedulers.single())
            .subscribe { voteSuccess ->
                if(!voteSuccess){
                    contextWrapper.toast("No Connection...")
                    if(isVoteUp){
                        clickVoteUtil(tvUpVote, resources, true, ideaPost.upVoteCount+1, true)
                        if(ideaPost.isMeVoteDown)
                            clickVoteUtil(tvDownVote, resources, false, ideaPost.downVoteCount-1, false)
                    }
                    else{
                        clickVoteUtil(tvDownVote, resources, false, ideaPost.downVoteCount+1, true)
                        if(ideaPost.isMeVoteUp)
                            clickVoteUtil(tvUpVote, resources, true, ideaPost.upVoteCount-1, false)
                    }
                }
            }
    }


    private fun clickVoteUtil(textView : FaSolidTextView, resources: Resources, isUpVote : Boolean, voteCount : Int, isVotedBefore : Boolean){

        val currentVoteCount = if(isVotedBefore) voteCount - 1
        else voteCount + 1


        setVoteText(textView, resources, isUpVote, currentVoteCount, !isVotedBefore)
    }


    fun setVoteText(textView : FaSolidTextView, resources: Resources, isUpVote : Boolean, voteCount : Int, isVoted : Boolean){
        if(isVoted){
            textView.setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
        }
        else{
            textView.setTextColor(ResourcesCompat.getColor(resources, R.color.grey, null))
        }

        val iconString = if(isUpVote) resources.getString(R.string.fa_vote_up)
        else resources.getString(R.string.fa_vote_down)

        val newText = "$iconString $voteCount"

        textView.text = newText
    }
}

