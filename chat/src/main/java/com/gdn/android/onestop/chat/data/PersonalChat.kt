package com.gdn.android.onestop.chat.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toDateString

@Entity
open class PersonalChat {
  @PrimaryKey
  lateinit var id: String
  lateinit var username: String
  lateinit var text: String
  var createdAt: Long = 0

  var isMe : Boolean = false
  var isReply : Boolean = false
  var repliedId: String? = null
  var repliedText: String? = null
  var repliedUsername: String? = null

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
}