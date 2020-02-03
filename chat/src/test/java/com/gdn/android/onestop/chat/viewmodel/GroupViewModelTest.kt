package com.gdn.android.onestop.chat.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.data.GroupRepository
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GroupViewModelTest {

  lateinit var viewmodel: ChatListViewModel

  private val mainThreadSurrogate = newSingleThreadContext("test-thread")

  @RelaxedMockK
  lateinit var groupRepository: GroupRepository

  @get:Rule
  val testRule = InstantTaskExecutorRule()

  private val GROUP_NAME = "groupName"
  private val GROUP_TYPE = Group.Type.TRIBE
  private val GROUP_CODE = "abc"
  private val GROUP_ID = "123"

  @Before
  fun setup(){
    Dispatchers.setMain(mainThreadSurrogate)
    MockKAnnotations.init(this)
    viewmodel = ChatListViewModel(groupRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    mainThreadSurrogate.close()
  }

  @Test
  fun refreshData_forceFalse(){
    runBlocking { viewmodel.refreshData(false) }
    coVerify { groupRepository.reloadGroup(false) }
  }

  @Test
  fun refreshData_forceTrue(){
    runBlocking { viewmodel.refreshData(true) }
    coVerify { groupRepository.reloadGroup(true) }
  }

  @Test
  fun createGroupTest(){
    runBlocking { viewmodel.createGroup(GROUP_NAME, GROUP_TYPE) }
    coVerify { groupRepository.createGroup(GROUP_NAME, GROUP_TYPE) }
  }

  @Test
  fun joinGroupTest(){
    runBlocking { viewmodel.joinGroup(GROUP_CODE) }
    coVerify { groupRepository.joinGroup(GROUP_CODE) }
  }

  @Test
  fun leaveGroupTest(){
    runBlocking { groupRepository.leaveGroup(GROUP_ID) }
    coVerify { groupRepository.leaveGroup(GROUP_ID) }
  }


}