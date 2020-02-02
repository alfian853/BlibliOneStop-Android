package com.gdn.android.onestop.chat.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName
import com.gdn.android.onestop.base.util.toDateString

@Entity
open class PersonalChat {
  @PrimaryKey
  lateinit var id: String
  var from: String = ""
  var to: String = ""
  lateinit var text: String
  var createdAt: Long = 0

  var isMe : Boolean = false
  var isReply : Boolean = false
  var repliedId: String? = null
  var repliedText: String? = null
  var repliedUsername: String? = null

  open fun getSenderName(): String {
    return from
  }

  @Ignore
  var isSending = false

  @delegate:Ignore
  val dayOfYear: String by lazy {
    createdAt.toDateString()
  }

  @delegate:Ignore
  val nameColor : Int by lazy {
    Util.getColorFromString(getSenderName())
  }

  @delegate:Ignore
  val alias : String by lazy {
    getSenderName().toAliasName()
  }

  @delegate:Ignore
  val repliedNameColor : Int by lazy {
      Util.getColorFromString(repliedUsername+"")
  }
}