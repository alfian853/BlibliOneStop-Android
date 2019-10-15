package com.gdn.android.onestop.idea.data

import androidx.paging.PagedList
import io.reactivex.rxjava3.schedulers.Schedulers

class IdeaBoundaryCallback(
    private val ideaChannelRepository: IdeaChannelRepository
) : PagedList.BoundaryCallback<IdeaPost>(){

    override fun onItemAtEndLoaded(itemAtEnd: IdeaPost) {
        ideaChannelRepository.loadMoreData().subscribeOn(Schedulers.io()).subscribe()
    }

}