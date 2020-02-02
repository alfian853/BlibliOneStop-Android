package com.gdn.android.onestop.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class GroupMeeting {

  @PrimaryKey
  lateinit var chatId: String

  lateinit var groupId: String
  lateinit var groupName: String
  var meetingDate: Long = 0
  var meetingNo: Int = 0
}