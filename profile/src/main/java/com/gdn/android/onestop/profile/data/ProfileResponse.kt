package com.gdn.android.onestop.profile.data

import com.gdn.android.onestop.ideation.data.IdeaPost
import java.util.*
import kotlin.collections.ArrayList

class ProfileResponse {
  var ideationPosts = 0
  var ideationComments = 0
  var readedBooks = 0
  var listenedAudios = 0
  var writtenMeetingNotes = 0
  var points = 0

  var topIdeas: List<IdeaPost> = ArrayList()
}