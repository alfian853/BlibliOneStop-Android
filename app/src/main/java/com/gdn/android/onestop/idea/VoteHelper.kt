package com.gdn.android.onestop.idea

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.gdn.android.onestop.R
import com.gdn.android.onestop.base.AwesomeTextView
import com.gdn.android.onestop.idea.data.IdeaPost
import io.reactivex.schedulers.Schedulers


private fun setVoteText(textView : AwesomeTextView, resources: Resources, isUpVote : Boolean, voteCount : Int, isVoted : Boolean){
    if(isVoted){
        textView.setTextColor(ResourcesCompat.getColor(resources, R.color.blue, null))
    }
    else{
        textView.setTextColor(ResourcesCompat.getColor(resources, R.color.grey, null))
    }

    val iconString = if(isUpVote) resources.getString(R.string.fa_vote_up)
                    else resources.getString(R.string.fa_vote_down)

    val newText = "$iconString $voteCount"

    textView.text = newText
}

fun clickVoteUtil(textView : AwesomeTextView, resources: Resources, isUpVote : Boolean, voteCount : Int, isVotedBefore : Boolean){

    val currentVoteCount = if(isVotedBefore) voteCount - 1
                           else voteCount + 1


    setVoteText(textView, resources, isUpVote, currentVoteCount, !isVotedBefore)
}


fun clickVote(ideaViewModel: IdeaViewModel, context: Context,
                  tvUpVote : AwesomeTextView, tvDownVote : AwesomeTextView,
                  resources: Resources, ideaPost: IdeaPost, isVoteUp: Boolean){

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

    ideaViewModel.voteIdeaPost(ideaPost, isVoteUp)
        .subscribeOn(Schedulers.single())
        .subscribe { voteSuccess ->
            if(!voteSuccess){
                Toast.makeText(context, "Vote Failed", Toast.LENGTH_SHORT).show()
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


fun IdeaRecyclerViewAdapter.IdeaViewHolder.setVoteText(ideaPost: IdeaPost){
    setVoteText(tvUpVote, itemView.resources, true, ideaPost.upVoteCount, ideaPost.isMeVoteUp)
    setVoteText(tvDownVote, itemView.resources, false, ideaPost.downVoteCount, ideaPost.isMeVoteDown)
}

fun IdeaDetailFragment.setVoteText(){
    Log.d("idea",(databinding.tvUpVote == null).toString())
    setVoteText(databinding.tvUpVote!!, resources, true, ideaPost.upVoteCount, ideaPost.isMeVoteUp)
    setVoteText(databinding.tvDownVote!!, resources, false, ideaPost.downVoteCount, ideaPost.isMeVoteDown)
}


fun IdeaChannelFragment.clickVote(ideaPost: IdeaPost,
                                      itemView: IdeaRecyclerViewAdapter.IdeaViewHolder,
                                      isVoteUp: Boolean){
    clickVote(ideaViewModel, context ?: return,
        itemView.tvUpVote, itemView.tvDownVote,
        resources, ideaPost, isVoteUp)
}


fun IdeaDetailFragment.clickVote(ideaPost: IdeaPost, isVoteUp: Boolean){
    clickVote(ideaViewModel, context ?: return,
        databinding.tvUpVote!!, databinding.tvDownVote!!,
        resources, ideaPost, isVoteUp)
}