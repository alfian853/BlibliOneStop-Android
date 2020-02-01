package com.gdn.android.onestop.group.service

import android.app.IntentService
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.RemoteInput
import com.gdn.android.onestop.group.ChatConstant
import com.gdn.android.onestop.group.data.ChatSendRequest
import com.gdn.android.onestop.group.data.GroupChatRepository
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.injection.GroupComponent
import com.gdn.android.onestop.group.util.ChatUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatReplyService : IntentService("ChatReplyService") {

  @Inject
  lateinit var groupChatRepository: GroupChatRepository

  override fun onHandleIntent(intent: Intent?) {
    GroupComponent.getInstance().inject(this)

    val group = intent!!.extras!!.get(ChatConstant.GROUP) as Group

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
      val remoteInput: Bundle? = RemoteInput.getResultsFromIntent(intent)

      if (remoteInput != null) {
        val replyText = remoteInput.getCharSequence(
          ChatConstant.KEY_TEXT_REPLY
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