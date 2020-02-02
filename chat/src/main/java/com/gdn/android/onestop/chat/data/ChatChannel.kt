package com.gdn.android.onestop.chat.data

import java.io.Serializable

open class ChatChannel : Serializable {
  lateinit var name: String
  var isMute = false
  var unreadChat = 0
}