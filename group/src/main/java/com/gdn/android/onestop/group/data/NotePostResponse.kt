package com.gdn.android.onestop.group.data

data class NotePostResponse(
  val isConflict: Boolean,
  val currentText: String,
  val currentLastUpdate: Long
)