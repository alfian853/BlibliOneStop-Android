package com.gdn.android.onestop.group

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.gdn.android.onestop.base.util.Navigator
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.databinding.ActivityGroupBinding
import com.gdn.android.onestop.group.fragment.GroupChatFragment
import com.gdn.android.onestop.group.fragment.GroupChatFragmentArgs

class ChatActivity : AppCompatActivity(){

  lateinit var hostFragment: NavHostFragment

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val group : Group by lazy {
      val tmp : ChatActivityArgs by navArgs()
      tmp.groupModel
    }

    DataBindingUtil.setContentView<ActivityGroupBinding>(this, R.layout.activity_group)

    hostFragment = supportFragmentManager
      .findFragmentById(R.id.nav_fragment_group) as NavHostFragment

    val navController = hostFragment.navController

    val inflater = navController.navInflater
    navController.graph = inflater.inflate(R.navigation.group_navigation)

    navController.navigate(R.id.groupChatFragment, GroupChatFragmentArgs(group).toBundle())
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