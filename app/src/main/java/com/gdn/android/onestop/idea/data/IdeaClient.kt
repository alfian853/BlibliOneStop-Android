package com.gdn.android.onestop.idea.data

import com.gdn.android.onestop.base.BaseResponse
import com.gdn.android.onestop.base.UrlConstant
import retrofit2.Call
import retrofit2.http.*


interface IdeaClient {

    @GET("/idea")
    fun getIdeaPosts(
        @Query("page") page : Int,
        @Query("item_per_page") itemPerPage : Int
    ): Call<BaseResponse<List<IdeaPost>>>

    @POST("/idea/{id}/vote")
    fun voteIdea(
        @Path("id") id: String,
        @Query("vote_up") isVoteUp: Boolean
    ) : Call<BaseResponse<Boolean>>

}