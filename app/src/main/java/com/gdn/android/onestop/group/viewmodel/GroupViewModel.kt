package com.gdn.android.onestop.group.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.data.GroupRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupViewModel
@Inject
constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {


    fun guildLiveData() = groupRepository.guildLiveData
    fun squadLiveData() = groupRepository.squadLiveData
    fun tribeLiveData() = groupRepository.tribeLiveData

    fun refreshData(){
        viewModelScope.launch {
            groupRepository.reloadGroup()
        }
    }

    suspend fun createGroup(groupName : String, groupType : Group.Type): Group? {
        return groupRepository.createGroup(groupName, groupType)
    }

}