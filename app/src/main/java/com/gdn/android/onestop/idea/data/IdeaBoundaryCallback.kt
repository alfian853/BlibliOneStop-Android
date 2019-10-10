package com.gdn.android.onestop.idea.data

import android.util.Log
import androidx.paging.PagedList
import io.reactivex.rxjava3.schedulers.Schedulers

class IdeaBoundaryCallback(
    private val ideaChannelRepository: IdeaChannelRepository
) : PagedList.BoundaryCallback<IdeaPost>(){

    override fun onItemAtEndLoaded(itemAtEnd: IdeaPost) {
        Log.d("ideahehe","load lagi dong")
        ideaChannelRepository.loadMoreData().subscribeOn(Schedulers.io()).subscribe()
    }

}