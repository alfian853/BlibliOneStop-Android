package com.gdn.android.onestop.group.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName
import com.gdn.android.onestop.base.util.toDateString

@Entity
class GroupChat {
  @PrimaryKey
  lateinit var id: String
  lateinit var groupId : String
  lateinit var username: String
  lateinit var text: String
  var createdAt: Long = 0

  var isMe : Boolean = false
  var isReply : Boolean = false
  var repliedId: String? = null
  var repliedText: String? = null
  var repliedUsername: String? = null
  var isMeeting : Boolean = false
  var meetingDate: Long? = null
  var meetingNo: Int? = null

  @Ignore
  var isSending = false

  @delegate:Ignore
  val dayOfYear: String by lazy {
    createdAt.toDateString()
  }

  @delegate:Ignore
  val nameColor : Int by lazy {
    Util.getColorFromString(username)
  }

  @delegate:Ignore
  val repliedNameColor : Int by lazy {
    Util.getColorFromString(repliedUsername+"")
  }

  @delegate:Ignore
  val nameAlias : String by lazy {
    username.toAliasName()
  }
}