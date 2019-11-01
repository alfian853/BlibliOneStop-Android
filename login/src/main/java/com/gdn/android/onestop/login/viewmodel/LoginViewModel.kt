package com.gdn.android.onestop.login.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.gdn.android.onestop.login.data.AuthClient
import com.gdn.android.onestop.login.data.LoginRequest
import com.gdn.android.onestop.base.ObservableViewModel
import com.gdn.android.onestop.base.util.DefaultContextWrapper
import com.gdn.android.onestop.base.util.SessionManager
import javax.inject.Inject

class LoginViewModel
@Inject
constructor(
    private val authClient: AuthClient,
    private val sessionManager: SessionManager
)
    : ObservableViewModel() {

    var contextWrapper: DefaultContextWrapper? = null

    var username : String = "user"
        @Bindable
        get(){return field}
        set(value) {
            field = value
            notifyPropertyChanged(BR.username)
        }

    var password : String = "user"
        @Bindable
        get(){return field}
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }


    suspend fun doLogin(): Boolean {
        val response = authClient.postLogin(
            LoginRequest(
                username,
                password
            )
        )
        if(response.isSuccessful){
            sessionManager.saveLoginSession(response.body()!!.data)
        }

        return response.isSuccessful
    }


}