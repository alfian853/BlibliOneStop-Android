package com.gdn.android.onestop.idea.data

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gdn.android.onestop.base.BaseResponse
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class IdeaChannelRepository @Inject constructor(
    private val ideaDao: IdeaDao,
    private val ideaClient: IdeaClient
) {

    private val ideaBoundaryCallback = IdeaBoundaryCallback(this)
    @SuppressLint("SimpleDateFormat")
    val simple = SimpleDateFormat("dd MMM yyyy HH:mm:ss")

    var lastPageRequest = 1
    var isFetching = false

    companion object {
        private const val ITEM_PER_PAGE = 2
        private const val TAG = "ideaRepository"
    }

    private var ideaLiveData :LiveData<PagedList<IdeaPost>>

    init {
        this.ideaLiveData = LivePagedListBuilder(
                ideaDao.getIdeaDataSourceFactory(), ITEM_PER_PAGE
            ).setBoundaryCallback(ideaBoundaryCallback).build()
    }

    fun save(ideaPost: IdeaPost){
        Single.create<Unit> {
            ideaDao.savePost(ideaPost)
        }.subscribeOn(Schedulers.single())
            .subscribe()
    }

    fun getIdeaLiveData() : LiveData<PagedList<IdeaPost>> = ideaLiveData

    fun reloadIdeaChannelData() : Single<Boolean> {
        return Single.create<Boolean> {s ->
            lastPageRequest = 1
            this.fetchMoreData().subscribeOn(Schedulers.io()).subscribe{ ideaList ->
                Single.create<Unit> {
                    if(ideaList.isNotEmpty()){
                        ideaDao.deleteAllIdeaPost()
                        ideaDao.savePost(ideaList)
                    }
                }.subscribeOn(Schedulers.io()).subscribe()
                s.onSuccess(true)
            }
        }
    }

    fun loadMoreData() : Single<Boolean> {
        return Single.create{ s ->
            this.fetchMoreData().subscribeOn(Schedulers.io())
                .doOnError { s.onSuccess(false) }
                .subscribe { ideaList ->
                    Single.create<Unit> {
                        ideaDao.savePost(ideaList)
                    }.subscribeOn(Schedulers.io()).subscribe()
                    s.onSuccess(true)
                }
        }
    }

    private fun fetchMoreData() : Single<List<IdeaPost>> {
        return Single.create{getMoreDataSub ->
            if(isFetching){
                getMoreDataSub.onSuccess(emptyList())
            }
            else{
                isFetching = true
                Log.d(TAG, "page : $lastPageRequest")
                ideaClient.getIdeaPosts(lastPageRequest, ITEM_PER_PAGE).enqueue(
                    object : Callback<BaseResponse<List<IdeaPost>>> {
                        override fun onFailure(call: Call<BaseResponse<List<IdeaPost>>>, t: Throwable) {
                            t.printStackTrace()
                            getMoreDataSub.onSuccess(emptyList())
                        }

                        override fun onResponse(
                            call: Call<BaseResponse<List<IdeaPost>>>,
                            response: Response<BaseResponse<List<IdeaPost>>>
                        ) {
                            if(response.isSuccessful && response.body()?.data != null){

                                response.body()?.data?.let { ideaList ->
                                    ideaList.forEach {
                                        val calender = Calendar.getInstance()
                                        calender.timeInMillis = it.createdAt.toLong()
                                        it.createdAt = simple.format(calender.time)
                                    }
                                    lastPageRequest++
                                    isFetching = false
                                    getMoreDataSub.onSuccess(ideaList)
                                }
                            }
                            else{
                                getMoreDataSub.onSuccess(emptyList())
                            }

                        }
                    }
                )

            }

        }


    }

}