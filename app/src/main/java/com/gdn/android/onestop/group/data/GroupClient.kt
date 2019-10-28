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

    @GET("/group/{groupId}/chat")
    suspend fun getGroupChat(
        @Path("groupId") groupId : String,
        @Query("before_time") beforeTime : Long?,
        @Query("after_time") afterTime : Long?,
        @Query("size") size : Int
    ) : Response<BaseResponse<List<GroupChatResponse>>>

    @GET("/group/last_update")
    suspend fun getLastUpdate() : Response<BaseResponse<Long>>

    @POST("/group/join_group")
    suspend fun joinGroup(
        @Query("group_code") groupCode : String
    ) : Response<BaseResponse<Group>>

    @POST("/group/{groupId}/leave")
    suspend fun leaveGroup(
        @Path("groupId") groupId: String
    ) : Response<BaseResponse<Boolean>>

}