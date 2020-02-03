package com.gdn.android.onestop.chat.data

import androidx.room.Entity
import androidx.room.Ignore
import com.gdn.android.onestop.base.util.toAliasName

@Entity
open class GroupChat : PersonalChat() {
  lateinit var groupId : String
  lateinit var username : String
  var isMeeting : Boolean = false
  var meetingDate: Long? = null
  var meetingNo: Int? = null

  @delegate:Ignore
  val nameAlias : String by lazy {
    username.toAliasName()
  }

  override fun getSenderName(): String {
    return username
  }
}