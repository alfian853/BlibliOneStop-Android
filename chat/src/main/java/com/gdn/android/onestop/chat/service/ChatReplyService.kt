package com.gdn.android.onestop.chat.service

import android.app.IntentService
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.RemoteInput
import com.gdn.android.onestop.chat.ChatConstant
import com.gdn.android.onestop.chat.data.ChatSendRequest
import com.gdn.android.onestop.chat.data.GroupChatRepository
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.injection.GroupComponent
import com.gdn.android.onestop.chat.util.ChatUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatReplyService : IntentService("ChatReplyService") {

  @Inject
  lateinit var groupChatRepository: GroupChatRepository

  override fun onHandleIntent(intent: Intent?) {
    GroupComponent.getInstance().inject(this)

    val group = intent!!.extras!!.get(com.gdn.android.onestop.chat.ChatConstant.GROUP) as Group

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
      val remoteInput: Bundle? = RemoteInput.getResultsFromIntent(intent)

      if (remoteInput != null) {
        val replyText = remoteInput.getCharSequence(
          com.gdn.android.onestop.chat.ChatConstant.KEY_TEXT_REPLY
        ).toString()

        val chatRequest = ChatSendRequest()
        chatRequest.text = replyText


        CoroutineScope(Dispatchers.IO).launch {
          groupChatRepository.sendChat(group.id, chatRequest)
          ChatUtil.notifyChat(this@ChatReplyService, resources, "You", replyText, group)
        }
      }
    }
  }

}