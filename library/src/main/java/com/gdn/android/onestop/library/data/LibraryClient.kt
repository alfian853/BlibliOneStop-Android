package com.gdn.android.onestop.library.data

import com.gdn.android.onestop.base.BaseResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface LibraryClient {

  @GET("/book")
  suspend fun getBooks(@Query("after_time")
  afterTime: Long): Response<BaseResponse<List<Book>>>

  @GET
  @Streaming
  fun downloadBook(@Url fileUrl: String): Call<ResponseBody>

  @GET("/audio")
  suspend fun getAudios(@Query("after_time")
  afterTime: Long): Response<BaseResponse<List<Audio>>>

  @GET
  @Streaming
  fun downloadAudio(@Url fileUrl: String): Call<ResponseBody>

  @POST("/book/{id}/finish")
  suspend fun postBookFinished(@Path("id") bookId: String)

  @POST("/audio/{id}/finish")
  suspend fun postAudioFinished(@Path("id") audioId: String)

}