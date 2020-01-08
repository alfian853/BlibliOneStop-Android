package com.gdn.android.onestop.base

import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName

data class User (
    var username : String = "",
    var token : String = "")
{
    @delegate:Transient
    val alias : String by lazy{ username.toAliasName() }

    @delegate:Transient
    val color : Int by lazy{Util.getColorFromString(username)}
}