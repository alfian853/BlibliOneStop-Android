package com.gdn.android.onestop.idea.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class IdeaPost {
    @PrimaryKey
    lateinit var id: String
    lateinit var username: String
    lateinit var content: String
    var commentCount: Int = 0
    var upVoteCount: Int = 0
    var isMeVoteUp: Boolean = false
    var isMeVoteDown: Boolean = false
    var downVoteCount: Int = 0
    lateinit var createdAt: String
}