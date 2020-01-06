package com.gdn.android.onestop.ideation.util


import com.gdn.android.onestop.base.BaseResponse
import com.gdn.android.onestop.base.FaSolidTextView
import com.gdn.android.onestop.base.util.DefaultContextWrapper
import com.gdn.android.onestop.ideation.data.IdeaChannelRepository
import com.gdn.android.onestop.ideation.data.IdeaClient
import com.gdn.android.onestop.ideation.data.IdeaPost
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Test

import org.junit.Before
import org.mockito.ArgumentMatchers.anyInt
import retrofit2.Response

class VoteHelperTest {

  @MockK(relaxed = true)
  lateinit var ideaRepository: IdeaChannelRepository

  @MockK(relaxed = true)
  lateinit var ideaClient: IdeaClient

  @MockK(relaxed = true)
  lateinit var tvUpVote: FaSolidTextView

  @MockK(relaxed = true)
  lateinit var tvDownVote: FaSolidTextView

  @MockK(relaxed = true)
  lateinit var contextWrapper: DefaultContextWrapper

  @MockK(relaxed = true)
  lateinit var voteHelper: VoteHelper

  lateinit var ideaPost: IdeaPost

  private val mainThreadSurrogate = newSingleThreadContext("test-thread")

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    mainThreadSurrogate.close()
  }

  @Before
  fun setup(){
    Dispatchers.setMain(mainThreadSurrogate)
    MockKAnnotations.init(this)
    voteHelper = VoteHelper(ideaRepository, ideaClient)

    ideaPost = IdeaPost().apply {
      id = "abc"
      username = "username"
      content = "content"
    }
  }

  @Test
  fun clickVote_voteUp_unVotedPost_failed(){
    val upVoteBefore = ideaPost.upVoteCount
    runBlocking {
      voteHelper.clickVote(tvUpVote, tvDownVote, contextWrapper, ideaPost, true)
    }

    verify { contextWrapper.toast(any()) }
    Assert.assertEquals(upVoteBefore, ideaPost.upVoteCount)
    Assert.assertFalse(ideaPost.isMeVoteUp)
    verify(exactly = 2) { tvUpVote.setTextColor(anyInt()) }
  }

  @Test
  fun clickVote_voteDown_unVotedPost_failed(){
    val downVoteBefore = ideaPost.downVoteCount
    runBlocking {
      voteHelper.clickVote(tvUpVote, tvDownVote, contextWrapper, ideaPost, false)
    }

    verify { contextWrapper.toast(any()) }
    Assert.assertEquals(downVoteBefore, ideaPost.downVoteCount)
    Assert.assertFalse(ideaPost.isMeVoteDown)
    verify(exactly = 2) { tvDownVote.setTextColor(anyInt()) }
  }

  @Test
  fun clickVote_voteUp_unVotedPost_success(){
    val upVoteBefore = ideaPost.upVoteCount

    every { ideaClient.voteIdea(ideaPost.id, true) } returns Response.success(
      BaseResponse.success(true)
    )

    runBlocking {
      voteHelper.clickVote(tvUpVote, tvDownVote, contextWrapper, ideaPost, true)
    }

    Assert.assertEquals(upVoteBefore+1, ideaPost.upVoteCount)
    Assert.assertTrue(ideaPost.isMeVoteUp)
    verify(exactly = 1) { tvUpVote.setTextColor(anyInt()) }
  }

  @Test
  fun clickVote_voteDown_unVotedPost_success(){
    val downVoteBefore = ideaPost.downVoteCount

    every { ideaClient.voteIdea(ideaPost.id, false) } returns Response.success(
      BaseResponse.success(true)
    )

    runBlocking {
      voteHelper.clickVote(tvUpVote, tvDownVote, contextWrapper, ideaPost, false)
    }

    Assert.assertEquals(downVoteBefore+1, ideaPost.downVoteCount)
    Assert.assertTrue(ideaPost.isMeVoteDown)
    verify(exactly = 1) { tvDownVote.setTextColor(anyInt()) }
  }

  @Test
  fun clickVote_voteUp_upVotedPost_success(){
    ideaPost.upVoteCount = 10
    val upVoteBefore = ideaPost.upVoteCount
    ideaPost.isMeVoteUp = true

    every { ideaClient.voteIdea(ideaPost.id, true) } returns Response.success(
      BaseResponse.success(true)
    )

    runBlocking {
      voteHelper.clickVote(tvUpVote, tvDownVote, contextWrapper, ideaPost, true)
    }

    Assert.assertEquals(upVoteBefore-1, ideaPost.upVoteCount)
    Assert.assertFalse(ideaPost.isMeVoteUp)
    verify(exactly = 1) { tvUpVote.setTextColor(anyInt()) }
  }

  @Test
  fun clickVote_voteDown_downVotedPost_success(){
    ideaPost.downVoteCount = 10
    val downVoteBefore = ideaPost.downVoteCount
    ideaPost.isMeVoteDown = true

    every { ideaClient.voteIdea(ideaPost.id, false) } returns Response.success(
      BaseResponse.success(true)
    )

    runBlocking {
      voteHelper.clickVote(tvUpVote, tvDownVote, contextWrapper, ideaPost, false)
    }

    Assert.assertEquals(downVoteBefore-1, ideaPost.downVoteCount)
    Assert.assertFalse(ideaPost.isMeVoteDown)
    verify(exactly = 1) { tvDownVote.setTextColor(anyInt()) }
  }

  @Test
  fun clickVote_voteUp_downVotedPost_success(){
    ideaPost.upVoteCount = 10
    ideaPost.downVoteCount = 10
    val upVoteBefore = ideaPost.upVoteCount
    val downVoteBefore = ideaPost.downVoteCount
    ideaPost.isMeVoteDown = true

    every { ideaClient.voteIdea(ideaPost.id, true) } returns Response.success(
      BaseResponse.success(true)
    )

    runBlocking {
      voteHelper.clickVote(tvUpVote, tvDownVote, contextWrapper, ideaPost, true)
    }

    Assert.assertEquals(upVoteBefore+1, ideaPost.upVoteCount)
    Assert.assertEquals(downVoteBefore-1, ideaPost.downVoteCount)
    Assert.assertTrue(ideaPost.isMeVoteUp)
    Assert.assertFalse(ideaPost.isMeVoteDown)
    verify(exactly = 1) { tvUpVote.setTextColor(anyInt()) }
    verify(exactly = 1) { tvDownVote.setTextColor(anyInt()) }
  }

  @Test
  fun clickVote_voteDown_upVotedPost_success(){
    ideaPost.upVoteCount = 10
    ideaPost.downVoteCount = 10
    val upVoteBefore = ideaPost.upVoteCount
    val downVoteBefore = ideaPost.downVoteCount
    ideaPost.isMeVoteUp = true

    every { ideaClient.voteIdea(ideaPost.id, false) } returns Response.success(
      BaseResponse.success(true)
    )

    runBlocking {
      voteHelper.clickVote(tvUpVote, tvDownVote, contextWrapper, ideaPost, false)
    }

    Assert.assertEquals(upVoteBefore-1, ideaPost.upVoteCount)
    Assert.assertEquals(downVoteBefore+1, ideaPost.downVoteCount)
    Assert.assertTrue(ideaPost.isMeVoteDown)
    Assert.assertFalse(ideaPost.isMeVoteUp)
    verify(exactly = 1) { tvUpVote.setTextColor(anyInt()) }
    verify(exactly = 1) { tvDownVote.setTextColor(anyInt()) }
  }



}
