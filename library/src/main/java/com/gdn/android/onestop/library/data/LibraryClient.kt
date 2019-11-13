package com.gdn.android.onestop.library.data

import com.gdn.android.onestop.base.BaseResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface LibraryClient {

  @GET("/book")
  suspend fun getBooks(@Query("after_time")
  afterTime: Long): Response<BaseResponse<List<Book>>>

  @GET
  @Streaming
  fun downloadBook(@Url fileUrl: String): Call<ResponseBody>

}