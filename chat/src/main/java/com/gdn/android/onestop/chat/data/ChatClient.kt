package com.gdn.android.onestop.chat.data

import com.gdn.android.onestop.base.BaseResponse
import retrofit2.Response
import retrofit2.http.*


interface ChatClient{

    @GET("/chat/group/{groupId}")
    suspend fun getGroupChat(
        @Path("groupId") groupId : String,
        @Query("before_time") beforeTime : Long?,
        @Query("after_time") afterTime : Long?,
        @Query("size") size : Int
    ) : Response<BaseResponse<List<GroupChatResponse>>>

    @POST("/chat/group/{groupId}")
    suspend fun postGroupChat(
        @Path("groupId") groupId: String,
        @Body groupChatSendRequest: ChatSendRequest
    ) : Response<BaseResponse<GroupChatResponse>>

    @POST("/chat/subscribe")
    suspend fun subscribeGroupsByToken(
        @Query("token")token : String
    ) : Response<BaseResponse<Boolean>>

}