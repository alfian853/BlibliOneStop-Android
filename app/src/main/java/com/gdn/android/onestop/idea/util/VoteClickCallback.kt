package com.gdn.android.onestop.idea.util

import com.gdn.android.onestop.idea.data.IdeaPost


interface VoteClickCallback {
    fun onVote(ideaPost: IdeaPost, item : IdeaRecyclerAdapter.IdeaViewHolder, isVoteUp: Boolean)
}