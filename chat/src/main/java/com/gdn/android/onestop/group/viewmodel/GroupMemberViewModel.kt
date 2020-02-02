package com.gdn.android.onestop.group.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.group.data.GroupClient
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupMemberViewModel @Inject constructor(private val groupClient: GroupClient) : BaseViewModel(){

  private val _membersLiveData: MutableLiveData<List<String>> = MutableLiveData()
  val membersLiveData: LiveData<List<String>> = _membersLiveData

  suspend fun fetchMember(groupId: String){

    val response = groupClient.getMembers(groupId)

    if(response.isSuccessful){
      _membersLiveData.postValue(response.body()!!.data)
    }

  }


}