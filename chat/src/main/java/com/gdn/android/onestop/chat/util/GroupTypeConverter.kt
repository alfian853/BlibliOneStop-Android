package com.gdn.android.onestop.chat.util

import androidx.room.TypeConverter
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.data.Group.Type.*

class GroupTypeConverter {

    @TypeConverter
    fun toType(type : Int) : Group.Type {
        return when(type){
            GUILD.code -> GUILD
            SQUAD.code -> SQUAD
            TRIBE.code -> TRIBE
            else -> GUILD
        }
    }

    @TypeConverter
    fun toInt(type : Group.Type) : Int {
        return type.code
    }
}