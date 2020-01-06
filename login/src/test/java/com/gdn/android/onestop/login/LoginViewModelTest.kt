package com.gdn.android.onestop.login

import com.gdn.android.onestop.base.BaseResponse
import com.gdn.android.onestop.base.User
import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.login.data.AuthClient
import com.gdn.android.onestop.login.viewmodel.LoginViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response


class LoginViewModelTest {

  @MockK(relaxed = true)
  lateinit var authClient: AuthClient

  @MockK(relaxed = true)
  lateinit var sessionManager: SessionManager

  lateinit var viewmodel: LoginViewModel

  @Before
  fun setup(){
    MockKAnnotations.init(this)
    viewmodel = LoginViewModel(authClient, sessionManager)
  }

  @Test fun addition_isCorrect() {
    runBlocking {
      val response: Response<BaseResponse<User>> = Response.success(BaseResponse.success(User().apply {
        username = "alfian"
        token = "abcde"
      }))

      coEvery { authClient.postLogin(any()) } returns response
      viewmodel.doLogin()
      coVerify { sessionManager.saveLoginSession(any()) }
    }
  }
}
