package com.gdn.android.onestop.chat.data

class UserGroupResponse {
    lateinit var username: String
    var lastUpdate : Long = 0
    lateinit var guilds: List<Group>
    lateinit var squads: List<Group>
    lateinit var tribes: List<Group>
}