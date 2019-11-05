package com.gdn.android.onestop.library.data

import com.gdn.android.onestop.base.BaseResponse
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface LibraryClient {

    @GET("/book")
    suspend fun getBooks(
        @Query("after_time") afterTime : Long
    ) : Response<BaseResponse<List<Book>>>

    @Streaming
    @GET
    fun downloadBook(@Url fileUrl: String) : Call<ResponseBody>

}