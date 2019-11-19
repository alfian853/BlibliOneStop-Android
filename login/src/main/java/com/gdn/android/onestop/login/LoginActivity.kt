package com.gdn.android.onestop.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.login.databinding.ActivityLoginBinding
import com.gdn.android.onestop.login.injection.DaggerLoginComponent
import com.gdn.android.onestop.login.viewmodel.LoginViewModel
import com.gdn.android.onestop.base.BaseComponent
import com.gdn.android.onestop.base.BaseActivity
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.Navigator
import dagger.android.AndroidInjector
import kotlinx.coroutines.launch
import javax.inject.Inject


class LoginActivity : BaseActivity<LoginActivity, ActivityLoginBinding>(){

    override fun activity(): LoginActivity {
        return this
    }

    override fun activityInjector(): AndroidInjector<LoginActivity> {
        return DaggerLoginComponent.factory().create(BaseComponent.getInstance()!!)
    }

    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(sessionManager.isLoggedIn){
            val intent = Navigator.getIntent(Navigator.Destination.MAIN_ACTIVITY)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        loginViewModel = ViewModelProvider(this, viewModelProviderFactory)
            .get(LoginViewModel::class.java)


        databinding.viewmodel = loginViewModel

        databinding.btnLogin.setOnClickListener {
            loginViewModel.launch {

                if(loginViewModel.doLogin()){
                    val intent = Navigator.getIntent(Navigator.Destination.MAIN_ACTIVITY)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this@LoginActivity, "Invalid sername/password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}