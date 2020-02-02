package com.gdn.android.onestop.chat.data

data class NotePostRequest(
  val meetingNumber: Int,
  val lastUpdate: Long,
  val note: String
)
