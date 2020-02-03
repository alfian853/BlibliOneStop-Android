package com.gdn.android.onestop.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gdn.android.onestop.chat.util.GroupTypeConverter

@Entity
class Group : ChatChannel() {
    @PrimaryKey
    lateinit var id: String
    lateinit var groupCode: String

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