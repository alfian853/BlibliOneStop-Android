package com.gdn.android.onestop.chat.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gdn.android.onestop.base.BaseResponse
import com.gdn.android.onestop.chat.data.*
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import retrofit2.Response

class GroupMemberViewModelTest {

  @RelaxedMockK
  lateinit var groupClient: GroupClient

  lateinit var viewmodel: com.gdn.android.onestop.chat.viewmodel.GroupMemberViewModel

  private val mainThreadSurrogate = newSingleThreadContext("test-thread")

  @get:Rule
  val testRule = InstantTaskExecutorRule()

  @Before
  fun setup(){
    Dispatchers.setMain(mainThreadSurrogate)
    MockKAnnotations.init(this)
    viewmodel = com.gdn.android.onestop.chat.viewmodel.GroupMemberViewModel(groupClient)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    mainThreadSurrogate.close()
  }

  @Test
  fun fetchMember_fetchSuccess(){
    val mockResponse = Response.success(BaseResponse.success(emptyList<String>()))
    coEvery { groupClient.getMembers(any()) } returns mockResponse
    var isPushed = false
    viewmodel.membersLiveData.observeForever {
      isPushed = true
    }

    runBlocking {
      viewmodel.fetchMember("")
    }

    Assert.assertTrue(isPushed)
  }

  @Test
  fun fetchMember_fetchFailed(){
    val mockResponse: Response<BaseResponse<List<String>>> = mockk()
    every { mockResponse.isSuccessful } returns false
    coEvery { groupClient.getMembers(any()) } returns mockResponse
    var isPushed = false
    viewmodel.membersLiveData.observeForever {
      isPushed = true
    }

    runBlocking {
      viewmodel.fetchMember("")
    }

    Assert.assertFalse(isPushed)
  }


}