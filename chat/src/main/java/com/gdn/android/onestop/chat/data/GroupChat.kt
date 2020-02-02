package com.gdn.android.onestop.chat.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName
import com.gdn.android.onestop.base.util.toDateString

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
}