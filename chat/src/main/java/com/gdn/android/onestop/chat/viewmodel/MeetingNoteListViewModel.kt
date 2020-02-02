package com.gdn.android.onestop.chat.viewmodel

import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.chat.data.MeetingNoteRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MeetingNoteListViewModel
@Inject constructor(private val meetingRepository: MeetingNoteRepository) : BaseViewModel(){

  suspend fun loadMeetingData(groupId: String){
      meetingRepository.reloadAllData(groupId)
  }

  fun getNoteListLiveData(groupId: String) = meetingRepository.getMeetingLiveData(groupId)

}