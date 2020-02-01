package com.gdn.android.onestop.group.viewmodel

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.base.util.toDateTime24String
import com.gdn.android.onestop.group.data.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MeetingNoteViewModel
@Inject constructor(
  private val meetingNoteRepository: MeetingNoteRepository,
  private val groupDao: GroupDao) : BaseViewModel(){

  var noteText: String = ""
    @Bindable
    get() {return field}
    set(value) {
      field = value
      notifyPropertyChanged(BR.noteText)
    }

  private var meetingNote: MeetingNote = MeetingNote()

  fun setMeetingNoteId(noteId: String) {
    launch {
      meetingNote = groupDao.getMeetingNoteLiveDataById(noteId)
      noteText = meetingNote.note
    }
  }

  suspend fun submitNote(): Boolean {
    val notePostRequest = NotePostRequest(meetingNote.meetingNumber, meetingNote.lastUpdate, noteText)
    val result: NotePostResponse = meetingNoteRepository.postMeetingNote(meetingNote.groupId, notePostRequest)
    meetingNote.lastUpdate = result.currentLastUpdate
    if(result.isConflict){
      noteText = "<last edit : ${Date().time.toDateTime24String()}>\n "+
          "$noteText\n"+
          "<your last edit : ${result.currentLastUpdate.toDateTime24String()}>\n " +
          "${result.currentText}\n"

      return false
    }
    else{
      return true
    }

  }
}