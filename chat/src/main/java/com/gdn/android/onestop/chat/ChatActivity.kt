package com.gdn.android.onestop.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.gdn.android.onestop.base.util.Navigator
import com.gdn.android.onestop.chat.databinding.ActivityGroupBinding
import com.gdn.android.onestop.chat.fragment.GroupChatFragment
import com.gdn.android.onestop.chat.fragment.GroupChatFragmentArgs
import com.gdn.android.onestop.chat.fragment.PersonalChatFragment
import com.gdn.android.onestop.chat.fragment.PersonalChatFragmentArgs

class ChatActivity : AppCompatActivity(){

  lateinit var hostFragment: NavHostFragment

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val args: ChatActivityArgs by navArgs()

    DataBindingUtil.setContentView<ActivityGroupBinding>(this,
      R.layout.activity_group
    )

    hostFragment = supportFragmentManager
      .findFragmentById(R.id.nav_fragment_group) as NavHostFragment

    val navController = hostFragment.navController

    val inflater = navController.navInflater
    navController.graph = inflater.inflate(R.navigation.chat_navigation)

    if(args.personalInfo != null){
      navController.navigate(R.id.personalChatFragment, PersonalChatFragmentArgs(args.personalInfo!!).toBundle())
    }
    else{
      navController.navigate(R.id.groupChatFragment, GroupChatFragmentArgs(args.groupModel!!).toBundle())
    }
  }

  override fun onBackPressed() {
    val lastFragment = hostFragment.childFragmentManager.fragments[0]
    if(lastFragment is GroupChatFragment || lastFragment is PersonalChatFragment){
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