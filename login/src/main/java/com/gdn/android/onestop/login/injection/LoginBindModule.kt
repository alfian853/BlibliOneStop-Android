package com.gdn.android.onestop.login.injection

import androidx.lifecycle.ViewModel
import com.gdn.android.onestop.base.ViewModelKey
import com.gdn.android.onestop.login.viewmodel.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LoginBindModule {


    @LoginScope
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel) : ViewModel

}