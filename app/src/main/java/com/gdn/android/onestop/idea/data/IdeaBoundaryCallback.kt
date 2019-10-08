package com.gdn.android.onestop.idea.data

import android.annotation.SuppressLint
import android.util.Log
import androidx.paging.PagedList
import com.gdn.android.onestop.base.BaseResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class IdeaBoundaryCallback(
    private val ideaClient: IdeaClient,
    private val ideaDao: IdeaDao
) : PagedList.BoundaryCallback<IdeaPost>(){

    @SuppressLint("SimpleDateFormat")
    val simple = SimpleDateFormat("dd MMM yyyy HH:mm:ss")

    var lastPageRequest = 1
    var isFetching = false


    override fun onZeroItemsLoaded() {
        Log.d(TAG,"onZeroItems")
        getMoreData().subscribeOn(Schedulers.io()).subscribe()
    }

    override fun onItemAtEndLoaded(itemAtEnd: IdeaPost) {
        Log.d(TAG,"onEndLoaded")
        getMoreData().subscribeOn(Schedulers.io()).subscribe()
    }

    fun getMoreData() : Single<Boolean> {
        return Single.create<Boolean>{getMoreDataSub ->
            if(isFetching){
                getMoreDataSub.onSuccess(true)
            }
            else{
                isFetching = true
                Log.d(TAG, "page : $lastPageRequest")
                ideaClient.getIdeaPosts(lastPageRequest, ITEM_PER_PAGE).enqueue(
                    object : Callback<BaseResponse<List<IdeaPost>>> {
                        override fun onFailure(call: Call<BaseResponse<List<IdeaPost>>>, t: Throwable) {
                            t.printStackTrace()
                            getMoreDataSub.onSuccess(false)
                        }

                        override fun onResponse(
                            call: Call<BaseResponse<List<IdeaPost>>>,
                            response: Response<BaseResponse<List<IdeaPost>>>
                        ) {
                            Log.d("idea","receive return")
                            if(response.body()?.data != null){

                                response.body()?.data?.let { ideaList ->
                                    Log.d("idea","saving")
                                    ideaList.forEach {
                                        val calender = Calendar.getInstance()
                                        calender.timeInMillis = it.createdAt.toLong()
                                        it.createdAt = simple.format(calender.time)
                                    }
                                    lastPageRequest++
                                    isFetching = false
                                    Single.create<Unit> {
                                        ideaDao.save(ideaList)
                                        it.onSuccess(Unit)
                                    }.subscribeOn(Schedulers.single()).subscribe{ _ ->
                                        Log.d("idea","save success")
                                        getMoreDataSub.onSuccess(true)
                                    }
                                }
                            }
                            else{
                                getMoreDataSub.onSuccess(true)
                            }

                        }
                    }
                )

            }

        }

    }

    companion object {
        private const val ITEM_PER_PAGE = 2
        private const val TAG = "IdeaBoundary"
    }

}