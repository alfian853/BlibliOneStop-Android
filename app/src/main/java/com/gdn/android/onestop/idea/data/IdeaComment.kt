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
class IdeaComment {
    @PrimaryKey
    lateinit var id : String
    lateinit var postId: String
    lateinit var username: String
    lateinit var text: String
    lateinit var date: String

    override fun hashCode(): Int {
        return username.hashCode() + date.hashCode() + postId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdeaComment

        if (id != other.id) return false
        if (postId != other.postId) return false
        if (username != other.username) return false
        if (text != other.text) return false
        if (date != other.date) return false

        return true
    }
}