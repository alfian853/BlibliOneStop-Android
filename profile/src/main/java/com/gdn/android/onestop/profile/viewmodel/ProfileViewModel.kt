package com.gdn.android.onestop.profile.viewmodel

import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.ideation.data.IdeaDao
import com.gdn.android.onestop.profile.data.ProfileClient
import com.gdn.android.onestop.profile.data.ProfileResponse
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
  private val profileClient: ProfileClient,
  private val ideaDao: IdeaDao
) : BaseViewModel() {

  private var profileResponse: ProfileResponse? = null
  var username: String? = null

  suspend fun getProfile(username: String): ProfileResponse {

      profileResponse = profileClient.getProfile(username).data
      this.username = username

      ideaDao.insertIdea(profileResponse!!.topIdeas)
    return profileResponse!!
  }

}