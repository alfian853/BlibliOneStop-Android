package com.gdn.android.onestop.idea.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class IdeaRepository @Inject constructor(
    private val ideaDao: IdeaDao,
    private val ideaClient: IdeaClient
) {

    private val boundaryCallback = IdeaBoundaryCallback(ideaClient, ideaDao)

    companion object {
        private const val ITEM_PER_PAGE = 2
        private const val TAG = "ideaRepository"
    }

    private var pagedIdeaList :LiveData<PagedList<IdeaPost>>

    init {
        Log.d(TAG,"ideaRepository created")
        this.pagedIdeaList = LivePagedListBuilder(
                ideaDao.getIdeaDataSourceFactory(), ITEM_PER_PAGE
            )
            .setBoundaryCallback(boundaryCallback)
            .build()
        pagedIdeaList.observeForever(object : Observer<PagedList<IdeaPost>>{
            override fun onChanged(t: PagedList<IdeaPost>) {
                boundaryCallback.lastPageRequest = ((t.size+ ITEM_PER_PAGE-1)/ ITEM_PER_PAGE) + 1
                pagedIdeaList.removeObserver(this)
            }
        })
    }

    fun save(ideaPost: IdeaPost){
        Single.create<Unit> {
            ideaDao.save(ideaPost)
        }.subscribeOn(Schedulers.single())
            .subscribe()
    }

    fun getIdeaData() : LiveData<PagedList<IdeaPost>>{
        return this.pagedIdeaList
    }

    fun reloadData() : Single<Boolean> {
        return Single.create<Boolean> {s ->
            ideaDao.deleteAll()
            boundaryCallback.lastPageRequest = 1
            boundaryCallback.getMoreData().subscribe{ isSuccess ->
                s.onSuccess(isSuccess)
            }
        }
    }


}