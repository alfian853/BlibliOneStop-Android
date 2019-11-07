package com.gdn.android.onestop.base

import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.base.util.toAliasName
import com.google.gson.annotations.Expose

class User {
    var username : String = ""

    lateinit var token : String
    lateinit var pict_url : String


    @delegate:Transient
    val alias : String by lazy{ username.toAliasName() }

    @delegate:Transient
    val color : Int by lazy{Util.getColorFromString(username)}
}