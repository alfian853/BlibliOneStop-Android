package com.gdn.android.onestop.group.viewmodel

import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.data.GroupRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupViewModel
@Inject
constructor(
    private val groupRepository: GroupRepository
) : BaseViewModel() {


    fun guildLiveData() = groupRepository.guildLiveData
    fun squadLiveData() = groupRepository.squadLiveData
    fun tribeLiveData() = groupRepository.tribeLiveData

    suspend fun refreshData(isForceUpdate : Boolean = false){
        groupRepository.reloadGroup(isForceUpdate)
    }

    suspend fun createGroup(groupName : String, groupType : Group.Type): Group? {
        return groupRepository.createGroup(groupName, groupType)
    }

    suspend fun joinGroup(groupCode : String): Group? {
        return groupRepository.joinGroup(groupCode)
    }

    suspend fun leaveGroup(groupId : String) {
        groupRepository.leaveGroup(groupId)
    }

}