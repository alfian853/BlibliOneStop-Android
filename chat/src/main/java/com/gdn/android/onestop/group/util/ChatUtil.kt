package com.gdn.android.onestop.group.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import com.gdn.android.onestop.base.Constant
import com.gdn.android.onestop.base.util.Navigator
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.group.ChatActivity
import com.gdn.android.onestop.group.ChatActivityArgs
import com.gdn.android.onestop.group.ChatConstant
import com.gdn.android.onestop.group.R
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.group.data.GroupChatResponse
import com.gdn.android.onestop.group.service.ChatReplyService
import com.gdn.android.onestop.group.data.Group

object ChatUtil {

  fun mapChatResponse(chatResponse: GroupChatResponse, username : String) : GroupChat {
    val groupChat = GroupChat()
    groupChat.id = chatResponse.id
    groupChat.username = chatResponse.username
    groupChat.isMe = username == chatResponse.username
    groupChat.text = chatResponse.text

    groupChat.createdAt = chatResponse.createdAt
    chatResponse.meetingDate?.let {
      groupChat.meetingDate = it
    }
    groupChat.isMeeting = chatResponse.isMeeting
    groupChat.isReply = chatResponse.isReply
    groupChat.repliedId = chatResponse.repliedId
    groupChat.repliedUsername = chatResponse. repliedUsername
    groupChat.repliedText = chatResponse.repliedText
    groupChat.meetingNo = chatResponse.meetingNo

    return groupChat
  }

  fun notifyChat(context: Context, resources: Resources, username: String, message: String, group: Group){
    val mainIntent = Intent(context, ChatActivity::class.java)
    mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    mainIntent.putExtras(ChatActivityArgs(group).toBundle())

    val mainPIntent: PendingIntent = PendingIntent.getActivity(
      context, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT
    )

    val replyLabel = "Enter your reply here"
    val remoteInput: RemoteInput = RemoteInput.Builder(ChatConstant.KEY_TEXT_REPLY).setLabel(replyLabel).build()

    val replyPendingIntent: PendingIntent = PendingIntent.getService(context, 0,
      Intent(context, ChatReplyService::class.java).apply {
        putExtra(ChatConstant.GROUP,group)
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

