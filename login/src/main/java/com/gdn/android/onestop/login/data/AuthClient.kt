package com.gdn.android.onestop.login.data

import com.gdn.android.onestop.base.BaseResponse
import com.gdn.android.onestop.base.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthClient {

    @POST("/auth/login")
    suspend fun postLogin(@Body loginRequest: LoginRequest) : Response<BaseResponse<User>>
}