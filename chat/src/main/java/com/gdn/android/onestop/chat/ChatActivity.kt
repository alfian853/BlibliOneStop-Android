package com.gdn.android.onestop.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.gdn.android.onestop.base.util.Navigator
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.fragment.GroupChatFragment
import com.gdn.android.onestop.chat.databinding.ActivityGroupBinding
import com.gdn.android.onestop.chat.fragment.GroupChatFragmentArgs

class ChatActivity : AppCompatActivity(){

  lateinit var hostFragment: NavHostFragment

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val group : Group by lazy {
      val tmp : com.gdn.android.onestop.chat.ChatActivityArgs by navArgs()
      tmp.groupModel
    }

    DataBindingUtil.setContentView<ActivityGroupBinding>(this,
      com.gdn.android.onestop.chat.R.layout.activity_group
    )

    hostFragment = supportFragmentManager
      .findFragmentById(com.gdn.android.onestop.chat.R.id.nav_fragment_group) as NavHostFragment

    val navController = hostFragment.navController

    val inflater = navController.navInflater
    navController.graph = inflater.inflate(com.gdn.android.onestop.chat.R.navigation.group_navigation)

    navController.navigate(com.gdn.android.onestop.chat.R.id.groupChatFragment, GroupChatFragmentArgs(group).toBundle())
  }

  override fun onBackPressed() {
    if(hostFragment.childFragmentManager.fragments[0] is GroupChatFragment){
      finish()

      if(isTaskRoot){
        val intent = Navigator.getIntent(Navigator.Destination.MAIN_ACTIVITY)
        startActivity(intent)
      }
    }
    else{
      hostFragment.navController.navigateUp()
    }
  }
}