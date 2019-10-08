package com.gdn.android.onestop.idea.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = IdeaPost::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IdeaComment(
    @PrimaryKey
    var postId : String,
    var username : String,
    var text : String
)