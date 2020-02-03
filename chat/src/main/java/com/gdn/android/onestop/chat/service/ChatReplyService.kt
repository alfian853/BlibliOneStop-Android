package com.gdn.android.onestop.chat.service

import android.app.IntentService
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.RemoteInput
import com.gdn.android.onestop.chat.ChatConstant
import com.gdn.android.onestop.chat.ChatConstant.GROUP
import com.gdn.android.onestop.chat.ChatConstant.PERSONAL_INFO
import com.gdn.android.onestop.chat.data.*
import com.gdn.android.onestop.chat.injection.ChatComponent
import com.gdn.android.onestop.chat.util.ChatUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatReplyService : IntentService("ChatReplyService") {

  @Inject
  lateinit var groupChatRepository: GroupChatRepository

  @Inject
  lateinit var personalChatRepository: PersonalChatRepository

  override fun onHandleIntent(intent: Intent?) {
    ChatComponent.getInstance().inject(this)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
      val remoteInput: Bundle? = RemoteInput.getResultsFromIntent(intent)

      if (remoteInput != null) {
        val replyText = remoteInput.getCharSequence(
          ChatConstant.KEY_TEXT_REPLY
        ).toString()

        val group = intent!!.extras!!.get(GROUP) as Group?
        val personalInfo = intent.extras!!.get(PERSONAL_INFO) as PersonalInfo?

        if(group != null){
          val chatRequest = GroupChatSendRequest()
          chatRequest.text = replyText

          CoroutineScope(Dispatchers.IO).launch {
            groupChatRepository.sendChat(group.id, chatRequest)
            ChatUtil.notifyGroupChat(this@ChatReplyService, "You", replyText, group)
          }
        }
        else if(personalInfo != null){
          val chatRequest = PersonalChatSendRequest()
          chatRequest.text = replyText

          CoroutineScope(Dispatchers.IO).launch {
            personalChatRepository.sendChat(personalInfo.name, chatRequest)
            ChatUtil.notifyPersonalChat(this@ChatReplyService,  replyText, personalInfo)
          }
        }

      }
    }
  }

}