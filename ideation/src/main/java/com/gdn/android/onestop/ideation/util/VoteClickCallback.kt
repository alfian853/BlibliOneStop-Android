package com.gdn.android.onestop.ideation.util

import com.gdn.android.onestop.ideation.data.IdeaPost


interface VoteClickCallback {
    fun onVote(ideaPost: IdeaPost, item : IdeaRecyclerAdapter.IdeaViewHolder, isVoteUp: Boolean)
}