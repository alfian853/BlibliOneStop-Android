package com.gdn.android.onestop.base

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gdn.android.onestop.base.util.SessionManager
import dagger.android.AndroidInjector
import javax.inject.Inject


abstract class BaseActivity<D : Activity, T : ViewDataBinding> : AppCompatActivity() {

    lateinit var databinding : T

    abstract fun getLayout() : Int

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        activityInjector().inject(activity())
        super.onCreate(savedInstanceState)
//        if(!sessionManager.isLoggedIn && this !is LoginActivity){
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            startActivity(intent)
//            finish()
//        }
        this.databinding = DataBindingUtil.setContentView(this, getLayout())
        this.databinding.lifecycleOwner = this
    }

    abstract fun activityInjector() : AndroidInjector<D>

    abstract fun activity() : D
}