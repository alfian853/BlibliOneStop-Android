package com.gdn.android.onestop.chat.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.gdn.android.onestop.base.Constant
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName
import com.gdn.android.onestop.chat.ChatActivity
import com.gdn.android.onestop.chat.ChatActivityArgs
import com.gdn.android.onestop.chat.ChatConstant
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
      repliedUsername = chatResponse.repliedUsername
    }
  }

  private fun buildActionNotification(replyPendingIntent: PendingIntent, context: Context): NotificationCompat.Action {
    val replyLabel = "Enter your reply here"
    val remoteInput: RemoteInput = RemoteInput.Builder(ChatConstant.KEY_TEXT_REPLY).setLabel(replyLabel).build()

    val action: NotificationCompat.Action = NotificationCompat.Action.Builder(
      R.drawable.ic_send,
      context.resources.getString(R.string.enter_reply), replyPendingIntent)
      .addRemoteInput(remoteInput)
      .build()

    return action
  }

  private fun buildPendingMainIntent(context: Context, args: ChatActivityArgs): PendingIntent {
    val mainIntent = Intent(context, ChatActivity::class.java)
    mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    mainIntent.putExtras(args.toBundle())

    return PendingIntent.getActivity(
      context, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT
    )

  }

  val backgroundColorMethod = "setBackgroundColor"

  fun notifyGroupChat(context: Context, username: String, message: String, group: Group, isYou: Boolean = false){

    val mainPIntent: PendingIntent = buildPendingMainIntent(context, ChatActivityArgs(group, null))

    val replyPendingIntent: PendingIntent = PendingIntent.getService(context, 0,
      Intent(context, ChatReplyService::class.java).apply {
        putExtra(ChatConstant.GROUP,group)
      },PendingIntent.FLAG_UPDATE_CURRENT
    )

    val action: NotificationCompat.Action = buildActionNotification(replyPendingIntent, context)

    val viewsmall = RemoteViews(context.packageName, R.layout.notification_chat_small)
    val viewlarge = RemoteViews(context.packageName, R.layout.notification_chat_large)

    val alias = username.toAliasName()

    viewsmall.setTextViewText(R.id.tv_user_alias, alias)
    viewlarge.setTextViewText(R.id.tv_user_alias, alias)

    val color = Util.getColorFromString(username)
    viewsmall.setInt(R.id.tv_user_alias, backgroundColorMethod, color)
    viewlarge.setInt(R.id.tv_user_alias, backgroundColorMethod, color)

    val title = if(isYou)"You · ${group.name}"
    else "$username · ${group.name}"

    viewsmall.setTextViewText(R.id.tv_title, title)
    viewlarge.setTextViewText(R.id.tv_title, title)

    viewlarge.setTextViewText(R.id.tv_message, message)
    viewsmall.setTextViewText(R.id.tv_message, message)

    val notificationManager =  NotificationManagerCompat.from(context)

    val notification = NotificationCompat.Builder(context, Constant.NOTIF_CHAT_CHANNEL_ID)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setSmallIcon(R.drawable.ic_send)
      .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
      .setStyle(NotificationCompat.DecoratedCustomViewStyle())
      .setCustomContentView(viewsmall)
      .setCustomBigContentView(viewlarge)
      .setContentIntent(mainPIntent)
      .setAutoCancel(true)
      .addAction(action)
      .build()

    notificationManager.notify(Constant.NOTIF_CHAT_ID, notification)
  }

  fun notifyPersonalChat(context: Context, message: String, personalInfo: PersonalInfo){
    val mainPIntent: PendingIntent = buildPendingMainIntent(context, ChatActivityArgs( null, personalInfo))

    val replyPendingIntent: PendingIntent = PendingIntent.getService(context, 0,
      Intent(context, ChatReplyService::class.java).apply {
        putExtra(ChatConstant.PERSONAL_INFO,personalInfo)
      },PendingIntent.FLAG_UPDATE_CURRENT
    )

    val action: NotificationCompat.Action = buildActionNotification(replyPendingIntent, context)

    val notificationManager =  NotificationManagerCompat.from(context)

    val viewsmall = RemoteViews(context.packageName, R.layout.notification_chat_small)
    val viewlarge = RemoteViews(context.packageName, R.layout.notification_chat_large)

    val alias = personalInfo.name.toAliasName()

    viewsmall.setTextViewText(R.id.tv_user_alias, alias)
    viewlarge.setTextViewText(R.id.tv_user_alias, alias)

    val color = Util.getColorFromString(personalInfo.name)
    viewsmall.setInt(R.id.tv_user_alias, backgroundColorMethod, color)
    viewlarge.setInt(R.id.tv_user_alias, backgroundColorMethod, color)

    viewsmall.setTextViewText(R.id.tv_title, personalInfo.name)
    viewlarge.setTextViewText(R.id.tv_title, personalInfo.name)

    viewlarge.setTextViewText(R.id.tv_message, message)
    viewsmall.setTextViewText(R.id.tv_message, message)

    val notification = NotificationCompat.Builder(context, Constant.NOTIF_CHAT_CHANNEL_ID)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setSmallIcon(R.drawable.ic_send)
      .setStyle(NotificationCompat.DecoratedCustomViewStyle())
      .setCustomContentView(viewsmall)
      .setCustomBigContentView(viewlarge)
      .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
      .setContentIntent(mainPIntent)
      .setAutoCancel(true)
      .addAction(action)
      .build()


    notificationManager.notify(Constant.NOTIF_CHAT_ID, notification)
  }

  fun setSoundIcon(view: View, isMute: Boolean){
    view.background = if(isMute) ResourcesCompat.getDrawable(view.resources, R.drawable.ic_sound, null)
    else ResourcesCompat.getDrawable(view.resources, R.drawable.ic_sound_disable, null)
  }

  fun setSoundIconLabel(view: TextView, isMute: Boolean){
    view.text = if(isMute)view.resources.getString(R.string.unmute_group)
    else view.resources.getString(R.string.mute_group)
  }

}

