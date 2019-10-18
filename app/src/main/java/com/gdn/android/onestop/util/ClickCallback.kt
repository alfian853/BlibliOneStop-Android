package com.gdn.android.onestop.util

import com.gdn.android.onestop.idea.util.IdeaRecyclerAdapter
import com.gdn.android.onestop.idea.data.IdeaPost

interface ItemClickCallback<T> {
    fun onItemClick(item : T, position : Int)
}

interface VoteClickCallback {
    fun onVote(ideaPost: IdeaPost, item : IdeaRecyclerAdapter.IdeaViewHolder, isVoteUp: Boolean)
}