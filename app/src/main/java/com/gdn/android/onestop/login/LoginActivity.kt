package com.gdn.android.onestop.login

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.R
import com.gdn.android.onestop.app.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseActivity
import com.gdn.android.onestop.databinding.ActivityLoginBinding
import com.gdn.android.onestop.util.DefaultContextWrapper
import javax.inject.Inject


class LoginActivity : BaseActivity<ActivityLoginBinding>(){
    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this, viewModelProviderFactory)
            .get(LoginViewModel::class.java)

        loginViewModel.contextWrapper = DefaultContextWrapper(this)

        this.databinding.viewmodel = loginViewModel

        Log.d("idea","masuk loginnn")
    }
}