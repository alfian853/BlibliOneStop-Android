package com.gdn.android.onestop.chat.data

data class NotePostResponse(
  val isConflict: Boolean,
  val currentText: String,
  val currentLastUpdate: Long
)