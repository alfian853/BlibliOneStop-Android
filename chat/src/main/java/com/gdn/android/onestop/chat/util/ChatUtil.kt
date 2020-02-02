package com.gdn.android.onestop.chat.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import com.gdn.android.onestop.base.Constant
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.chat.ChatActivityArgs
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.data.*
import com.gdn.android.onestop.chat.service.ChatReplyService

object ChatUtil {

  fun mapGroupChatResponse(chatResponse: GroupChatResponse, username : String) : GroupChat {

    return GroupChat().apply {
      id = chatResponse.id
      this.username = chatResponse.username
      isMe = username == chatResponse.username
      text = chatResponse.text

      createdAt = chatResponse.createdAt
      chatResponse.meetingDate?.let {
        meetingDate = it
      }
      isMeeting = chatResponse.isMeeting
      isReply = chatResponse.isReply
      repliedId = chatResponse.repliedId
      repliedUsername = chatResponse. repliedUsername
      repliedText = chatResponse.repliedText
      meetingNo = chatResponse.meetingNo

    }
  }

  fun mapPersonalChatResponse(chatResponse: PersonalChatResponse, isMe: Boolean) : PersonalChat {
    return PersonalChat().apply {
      id = chatResponse.id
      this.isMe = isMe
      if(isMe){
        to = chatResponse.to
      }
      else{
        from = chatResponse.from
      }
      text = chatResponse.text
      createdAt = chatResponse.createdAt

      isReply = chatResponse.isReply
      repliedId = chatResponse.repliedId
      repliedText = chatResponse.repliedText
    }
  }

  fun notifyChat(context: Context, resources: Resources, username: String, message: String, group: Group){
    val mainIntent = Intent(context, com.gdn.android.onestop.chat.ChatActivity::class.java)
    mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    mainIntent.putExtras(ChatActivityArgs(group, null).toBundle())

    val mainPIntent: PendingIntent = PendingIntent.getActivity(
      context, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT
    )

    val replyLabel = "Enter your reply here"
    val remoteInput: RemoteInput = RemoteInput.Builder(com.gdn.android.onestop.chat.ChatConstant.KEY_TEXT_REPLY).setLabel(replyLabel).build()

    val replyPendingIntent: PendingIntent = PendingIntent.getService(context, 0,
      Intent(context, ChatReplyService::class.java).apply {
        putExtra(com.gdn.android.onestop.chat.ChatConstant.GROUP,group)
      }
      ,PendingIntent.FLAG_UPDATE_CURRENT
    )

    val action: NotificationCompat.Action = NotificationCompat.Action.Builder(
      R.drawable.ic_send,
      resources.getString(R.string.enter_reply), replyPendingIntent)
      .addRemoteInput(remoteInput)
      .build()

    val notificationManager =  NotificationManagerCompat.from(context)

    val notification = NotificationCompat.Builder(context, Constant.NOTIF_CHAT_CHANNEL_ID)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setSmallIcon(R.drawable.ic_group_thin)
      .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
      .setContentTitle(group.name)
      .setContentText(username+": "+ Util.shrinkText(message))
      .setContentIntent(mainPIntent)
      .setGroup(group.id)
      .setAutoCancel(true)
      .addAction(action)
      .build()


    notificationManager.notify(Constant.NOTIF_CHAT_ID, notification)
  }


}

