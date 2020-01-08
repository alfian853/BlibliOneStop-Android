package com.gdn.android.onestop.ideation.viewmodel

import com.gdn.android.onestop.ideation.data.IdeaChannelRepository
import com.gdn.android.onestop.ideation.viewmodel.IdeaCreateViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After

import org.junit.Before
import org.junit.Test

class IdeaCretelViewModelTest {

  @MockK(relaxed = true)
  lateinit var ideaRepository: IdeaChannelRepository

  lateinit var viewModel: IdeaCreateViewModel

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
    viewModel = IdeaCreateViewModel(ideaRepository)
  }

  @Test
  fun postIdeaTest(){
    viewModel.ideaContent = "testcontent"
    runBlocking { viewModel.postIdea() }

    coVerify { ideaRepository.postIdea("testcontent") }
  }


}
