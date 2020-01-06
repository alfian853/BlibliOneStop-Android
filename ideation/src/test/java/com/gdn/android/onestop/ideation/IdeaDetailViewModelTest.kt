package com.gdn.android.onestop.ideation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gdn.android.onestop.base.util.NetworkUtil
import com.gdn.android.onestop.ideation.data.IdeaCommentRepository
import com.gdn.android.onestop.ideation.viewmodel.IdeaDetailViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class IdeaDetailViewModelTest {

  @MockK(relaxed = true)
  lateinit var commentRepository: IdeaCommentRepository

  @MockK(relaxed = true)
  lateinit var networkUtil: NetworkUtil

  lateinit var viewModel: IdeaDetailViewModel

  private val mainThreadSurrogate = newSingleThreadContext("test-thread")

  @get:Rule
  val testRule = InstantTaskExecutorRule()

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    mainThreadSurrogate.close()
  }

  @Before
  fun setup(){
    Dispatchers.setMain(mainThreadSurrogate)
    MockKAnnotations.init(this)
    viewModel = IdeaDetailViewModel(commentRepository, networkUtil)
  }

  @Test
  fun setIdeaPostIdTest(){
    val postId = "abc"

    viewModel.setIdeaPostId(postId)

    verify { commentRepository.setIdeaPost(postId) }
  }

  @Test
  fun loadMoreComment_And_ExecuteIfOnline_isOnline(){
    viewModel.isNotLoading = true
    every { networkUtil.isConnectedToNetwork() } returns true
    viewModel.loadMoreComment()
    coVerify { commentRepository.loadMoreComment() }
  }

  @Test
  fun loadMoreComment_And_ExecuteIfOnline_isNotOnline(){
    viewModel.isNotLoading = true
    every { networkUtil.isConnectedToNetwork() } returns false
    viewModel.loadMoreComment()
    coVerify(exactly = 0) { commentRepository.loadMoreComment() }
  }

  @Test
  fun submitComment_success(){
    val comment = "comment"
    viewModel.commentInput = comment
    every { networkUtil.isConnectedToNetwork() } returns true
    coEvery { commentRepository.submitComment(comment) } returns true

    runBlocking { viewModel.submitComment {} }

    coVerify { commentRepository.submitComment(comment) }
  }

  @Test
  fun submitComment_failed(){

    val comment = "comment"
    viewModel.commentInput = comment
    every { networkUtil.isConnectedToNetwork() } returns true
    coEvery { commentRepository.submitComment(comment) } returns false

    runBlocking { viewModel.submitComment {} }

    coVerify { commentRepository.submitComment(comment) }
  }


}
