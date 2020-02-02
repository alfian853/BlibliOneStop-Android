package com.gdn.android.onestop.group.data

import com.gdn.android.onestop.base.BaseResponse
import retrofit2.Response
import retrofit2.http.*


interface GroupClient{

    @POST("/chat/subscribe")
    suspend fun subscribeGroupsByToken(
        @Query("token")token : String
    ) : Response<BaseResponse<Boolean>>

    @GET("/group")
    suspend fun getGroups() : Response<BaseResponse<UserGroupResponse>>

    @POST("/group")
    suspend fun createGroups(
        @Body createGroupRequest: CreateGroupRequest
    ) : Response<BaseResponse<Group>>

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

    @GET("/group/{groupId}/meeting")
    suspend fun getMeetingNoteList(
        @Path("groupId") groupId: String
    ) : Response<BaseResponse<List<MeetingNote>>>

    @GET("/group/{groupId}/meeting/{meetingNo}")
    suspend fun getMeetingNote(
        @Path("groupId") groupId: String,
        @Path("meetingNo") meetingNo: Int
    ) : Response<BaseResponse<MeetingNote>>

    @POST("/group/{groupId}/meeting/note")
    suspend fun postMeetingNote(
        @Path("groupId") groupId: String,
        @Body notePostRequest: NotePostRequest
    ) : Response<BaseResponse<NotePostResponse>>

    @GET("/group/{groupId}/members")
    suspend fun getMembers(
        @Path("groupId") groupId: String
    ) : Response<BaseResponse<List<String>>>

}