package com.gdn.android.onestop.group.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.group.data.GroupChatRepository
import com.gdn.android.onestop.group.data.GroupDao
import com.gdn.android.onestop.group.data.GroupInfo
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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

class GroupChatViewModelTest {

  @MockK(relaxed = true)
  lateinit var groupDao: GroupDao

  @MockK(relaxed = true)
  lateinit var groupChatRepository: GroupChatRepository

  lateinit var viewmodel: GroupChatViewModel

  val GROUP_ID = "123"

  private val mainThreadSurrogate = newSingleThreadContext("test-thread")

  @get:Rule
  val testRule = InstantTaskExecutorRule()

  @Before
  fun setup(){
    Dispatchers.setMain(mainThreadSurrogate)
    MockKAnnotations.init(this)
    viewmodel = GroupChatViewModel(groupDao, groupChatRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    mainThreadSurrogate.close()
  }

  @Test
  fun resetStateAndGetLiveData(){

    coEvery { groupDao.getGroupInfo(GROUP_ID) } returns GroupInfo()

    runBlocking { viewmodel.resetStateAndGetLiveData(GROUP_ID) }

    coVerify { groupChatRepository.loadMoreChatBefore(GROUP_ID) }


    coEvery { groupDao.getGroupInfo(GROUP_ID) } returns GroupInfo().apply {
      this.lowerBoundTimeStamp = 0
    }

    runBlocking { viewmodel.resetStateAndGetLiveData(GROUP_ID) }

    coVerify { groupChatRepository.loadMoreChatAfter(GROUP_ID) }
  }

}