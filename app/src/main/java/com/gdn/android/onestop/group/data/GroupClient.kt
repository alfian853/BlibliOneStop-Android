package com.gdn.android.onestop.group.data

import com.gdn.android.onestop.base.BaseResponse
import retrofit2.Response
import retrofit2.http.*


interface GroupClient{

    @GET("/group")
    suspend fun getGroups() : Response<BaseResponse<UserGroupResponse>>

    @POST("/group")
    suspend fun createGroups(
        @Body createGroupRequest: CreateGroupRequest
    ) : Response<BaseResponse<Group>>

    @GET("/group/{groupId}")
    suspend fun getGroupChat(
        @Path("groupId") groupId : String,
        @Query("fromTime") fromMilliTime : Long
    ) : Response<BaseResponse<List<Chat>>>
}