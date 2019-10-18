package com.gdn.android.onestop.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gdn.android.onestop.login.LoginActivity
import com.gdn.android.onestop.util.SessionManager
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity(), HasAndroidInjector {
    lateinit var databinding : T

    abstract fun getLayout() : Int

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any?>

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        if(!sessionManager.isLoggedIn && this !is LoginActivity){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
        this.databinding = DataBindingUtil.setContentView(this, getLayout())
        this.databinding.lifecycleOwner = this
    }

    override fun androidInjector(): AndroidInjector<Any?> {
        return androidInjector
    }
}