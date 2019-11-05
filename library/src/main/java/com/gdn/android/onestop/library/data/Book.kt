package com.gdn.android.onestop.library.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.gdn.android.onestop.base.UrlConstant

@Entity
class Book {

    @PrimaryKey
    lateinit var id: String
    lateinit var title: String
    var totalPages: Int = 0
    lateinit var path: String
    lateinit var thumbnail: String
    var createdAt: Long = 0
    var fileSize: Long = 0
    var isDownloaded = false

    @delegate:Ignore
    val thumbnailUrl : String by lazy {
        UrlConstant.BASE_RESOURCE_URL  + thumbnail
    }

    @delegate:Ignore
    val fileUrl : String by lazy {
        UrlConstant.BASE_RESOURCE_URL + path
    }
}