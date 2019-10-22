package com.gdn.android.onestop.group.data

import androidx.lifecycle.LiveData

class GroupRepository(
        private val groupDao: GroupDao,
        private val groupClient: GroupClient
    ) {


    val guildLiveData : LiveData<List<Group>> = groupDao.getGroupByType(Group.Type.GUILD.code)
    val squadLiveData : LiveData<List<Group>> = groupDao.getGroupByType(Group.Type.SQUAD.code)
    val tribeLiveData : LiveData<List<Group>> = groupDao.getGroupByType(Group.Type.TRIBE.code)



    suspend fun reloadGroup(){
        val response = groupClient.getGroups()

        if(!response.isSuccessful)return

        response.body()?.data?.let {

            it.groups.forEach {group ->
                group.type = Group.Type.GUILD
            }

            it.squads.forEach {squad ->
                squad.type = Group.Type.SQUAD
            }

            it.tribes.forEach {tribe ->
                tribe.type = Group.Type.TRIBE
            }

            groupDao.insertGroup(it.groups+it.squads+it.tribes)
        }
    }

    suspend fun createGroup(groupName : String, groupType : Group.Type) : Group? {

        val response = groupClient.createGroups(CreateGroupRequest(groupName, groupType))


        if(response.isSuccessful){
            val group = response.body()!!.data!!

            groupDao.insertGroup(group)

            return group
        }

        return null
    }


}