package com.gdn.android.onestop.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class MeetingNote : Serializable{

  lateinit var groupId: String

  //chat id
  @PrimaryKey
  lateinit var id: String

  var meetingNumber: Int = 0

  var note: String = ""

  var lastUpdate: Long = 0

  var meetingDate: Long = 0

}