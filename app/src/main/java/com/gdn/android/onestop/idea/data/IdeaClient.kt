package com.gdn.android.onestop.idea.data

import com.gdn.android.onestop.base.BaseResponse
import com.gdn.android.onestop.base.UrlConstant
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface IdeaClient {

    @GET("/idea")
    suspend fun getIdeaPosts(
        @Query("page") page : Int,
        @Query("item_per_page") itemPerPage : Int
    ): Response<BaseResponse<List<IdeaPostResponse>>>

    @POST("/idea/{id}/vote")
    fun voteIdea(
        @Path("id") id: String,
        @Query("vote_up") isVoteUp: Boolean
    ) : Call<BaseResponse<Boolean>>

    @GET("/idea/{id}/comment")
    suspend fun getComment(
        @Path("id") id : String,
        @Query("page") page : Int,
        @Query("item_per_page") itemPerPage: Int
    ) : BaseResponse<List<IdeaComment>>

    @POST("/idea/{id}/comment")
    suspend fun postComment(
        @Path("id") id : String,
        @Body commentPostRequest: CommentPostRequest
    ) : Response<BaseResponse<IdeaComment>>

    @POST("/idea")
    suspend fun postIdea(@Body ideaPostRequest: IdeaPostRequest)
            : Response<BaseResponse<IdeaPostResponse>>
}