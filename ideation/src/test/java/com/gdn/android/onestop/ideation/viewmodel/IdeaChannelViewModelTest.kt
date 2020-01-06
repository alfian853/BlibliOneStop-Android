package com.gdn.android.onestop.ideation.viewmodel

import com.gdn.android.onestop.base.util.NetworkUtil
import com.gdn.android.onestop.ideation.data.IdeaChannelRepository
import com.gdn.android.onestop.ideation.viewmodel.IdeaChannelViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test

import org.junit.Before


class IdeaChannelViewModelTest {

  @MockK(relaxed = true)
  lateinit var ideaRepository: IdeaChannelRepository

  @MockK(relaxed = true)
  lateinit var networkUtil: NetworkUtil

  lateinit var viewModel: IdeaChannelViewModel

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
    viewModel = IdeaChannelViewModel(ideaRepository, networkUtil)
  }

  @Test
  fun getIdeaLiveData_hasInternet() {
    every { networkUtil.isConnectedToNetwork() } returns true
    runBlocking { viewModel.getIdeaLiveData() }
    coVerify { ideaRepository.reloadIdeaChannelData() }

  }

  @Test
  fun getIdeaLiveData_hasNoInternet() {
    every { networkUtil.isConnectedToNetwork() } returns false
    runBlocking { viewModel.getIdeaLiveData() }
    coVerify(exactly = 0) { ideaRepository.reloadIdeaChannelData() }
    verify { networkUtil.isConnectedToNetwork() }
  }

  @Test
  fun loadMoreDataTest(){
    runBlocking { viewModel.loadMoreData() }
    coVerify { ideaRepository.loadMoreData() }
  }

}
