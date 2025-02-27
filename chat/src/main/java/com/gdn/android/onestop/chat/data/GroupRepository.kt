package com.gdn.android.onestop.chat.data

import androidx.lifecycle.LiveData
import java.util.*

class GroupRepository(
    private val groupDao: GroupDao,
    private val groupClient: GroupClient,
    private val groupUpdateManager: GroupUpdateManager
) {
    val guildLiveData : LiveData<List<Group>> = groupDao.getGroupByType(Group.Type.GUILD.code)
    val squadLiveData : LiveData<List<Group>> = groupDao.getGroupByType(Group.Type.SQUAD.code)
    val tribeLiveData : LiveData<List<Group>> = groupDao.getGroupByType(Group.Type.TRIBE.code)

    private suspend fun checkIfCurrentDataUpToDate() : Boolean{
        val response = groupClient.getLastUpdate()
        if(response.isSuccessful){
            return groupUpdateManager.getLastUpdate() >= response.body()!!.data!!
        }
        return false
    }

    suspend fun reloadGroup(isForceUpdate : Boolean = false){

        val isUpToDate = !isForceUpdate && checkIfCurrentDataUpToDate()
        if(isUpToDate)return

        val response = groupClient.getGroups()

        if(!response.isSuccessful)return

        response.body()?.data?.let {
            groupUpdateManager.setLastUpdate(it.lastUpdate)

            it.guilds.forEach { group ->
                group.type = Group.Type.GUILD
                val info = groupDao.getGroupInfo(group.id)
                group.isMute = info.isMute
                group.unreadChat = info.unreadChat
            }

            it.squads.forEach {squad ->
                squad.type = Group.Type.SQUAD
                val info = groupDao.getGroupInfo(squad.id)
                squad.isMute = info.isMute
                squad.unreadChat = info.unreadChat
            }

            it.tribes.forEach {tribe ->
                tribe.type = Group.Type.TRIBE
                val info = groupDao.getGroupInfo(tribe.id)
                tribe.isMute = info.isMute
                tribe.unreadChat = info.unreadChat
            }

            groupDao.deleteAllGroup()
            groupDao.insertGroups(it.tribes, it.squads, it.guilds)
        }
    }

    suspend fun createGroup(groupName : String, groupType : Group.Type) : Group? {

        val response = groupClient.createGroups(
            CreateGroupRequest(
                groupName,
                groupType
            )
        )

        if(response.isSuccessful){
            groupUpdateManager.setLastUpdate(Date().time)

            val group = response.body()!!.data!!
            groupDao.insertGroup(group)

            return group
        }

        return null
    }

    suspend fun joinGroup(groupCode : String) : Group? {
        val response = groupClient.joinGroup(groupCode)

        if(response.isSuccessful){
            groupUpdateManager.setLastUpdate(Date().time)

            val group = response.body()!!.data!!
            groupDao.insertGroup(group)

            return group
        }

        return null
    }

    suspend fun leaveGroup(groupId : String) {
        val response = groupClient.leaveGroup(groupId)

        if(response.isSuccessful){
            groupUpdateManager.setLastUpdate(Date().time)
            groupDao.deleteGroupData(groupId)
        }
    }

}