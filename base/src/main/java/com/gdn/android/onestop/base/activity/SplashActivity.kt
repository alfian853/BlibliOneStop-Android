package com.gdn.android.onestop.base.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gdn.android.onestop.base.AppComponent
import com.gdn.android.onestop.base.util.Navigator
import com.gdn.android.onestop.base.util.SessionManager
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

  @Inject
  lateinit var sessionManager: SessionManager

  override fun onCreate(savedInstanceState: Bundle?) {
    AppComponent.getInstance()!!.inject(this)
    super.onCreate(savedInstanceState)

    if(sessionManager.isLoggedIn){
      val intent = Navigator.getIntent(Navigator.Destination.MAIN_ACTIVITY)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      startActivity(intent)
      finish()
    }
    else{
      val intent = Navigator.getIntent(Navigator.Destination.LOGIN)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      startActivity(intent)
      finish()
    }

  }
}