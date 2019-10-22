package com.gdn.android.onestop.login

import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.viewModelScope
import com.gdn.android.onestop.app.MainActivity
import com.gdn.android.onestop.util.SessionManager
import com.gdn.android.onestop.base.ObservableViewModel
import com.gdn.android.onestop.login.data.AuthClient
import com.gdn.android.onestop.login.data.LoginRequest
import com.gdn.android.onestop.util.DefaultContextWrapper
import kotlinx.coroutines.launch
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


    fun doLogin(){
        viewModelScope.launch {
            val response = authClient.postLogin(LoginRequest(username, password))
            if(response.isSuccessful){
                sessionManager.saveLoginSession(response.body()!!.data)
                contextWrapper!!.startActivity(MainActivity::class.java)
            }
            else{
                contextWrapper!!.toast("Invalid sername/password", Toast.LENGTH_SHORT)
            }
        }
    }


}