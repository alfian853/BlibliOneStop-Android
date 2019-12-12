package com.gdn.android.onestop.profile.viewmodel

import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.profile.data.ProfileClient
import com.gdn.android.onestop.profile.data.ProfileResponse
import javax.inject.Inject

class ProfileViewModel @Inject constructor(private val profileClient: ProfileClient) : BaseViewModel() {

  var profileResponse: ProfileResponse? = null
  var username: String? = null

  suspend fun getProfile(username: String): ProfileResponse {

//    if(profileResponse == null || this.username == null){
      profileResponse = profileClient.getProfile(username).data
      this.username = username
//    }

    return profileResponse!!
  }

}