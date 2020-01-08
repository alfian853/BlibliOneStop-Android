package com.gdn.android.onestop.profile.data

import com.gdn.android.onestop.base.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileClient {

  @GET("/profile/{username}")
  suspend fun getProfile(@Path("username") username: String): BaseResponse<ProfileResponse>

}