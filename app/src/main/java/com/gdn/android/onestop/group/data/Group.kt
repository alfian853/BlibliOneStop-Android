package com.gdn.android.onestop.group.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gdn.android.onestop.group.util.GroupTypeConverter
import java.util.*

@Entity
class Group {
    @PrimaryKey
    lateinit var id: String
    lateinit var name: String
    lateinit var groupCode : String

    var lastChatUpdate : Long = 0

    @TypeConverters(GroupTypeConverter::class)
    lateinit var type : Type

    enum class Type{
        GUILD(0),SQUAD(1),TRIBE(2);

        val code : Int
        constructor(code : Int){
            this.code = code
        }
    }
}