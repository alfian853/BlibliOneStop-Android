package com.gdn.android.onestop.chat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gdn.android.onestop.base.util.toAliasName
import java.io.Serializable

@Entity
class PersonalInfo : ChatChannel(), Serializable {
    @PrimaryKey
    lateinit var id : String

    @delegate:Transient
    val alias : String by lazy{ name.toAliasName() }


}