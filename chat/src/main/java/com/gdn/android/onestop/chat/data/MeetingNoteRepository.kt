package com.gdn.android.onestop.chat.data

class MeetingNoteRepository constructor(
  private val groupDao: GroupDao,
  private val groupClient: GroupClient
){

  fun getMeetingLiveData(groupId: String) = groupDao.getMeetingNoteLiveData(groupId)

  suspend fun reloadAllData(groupId: String){

    val response = groupClient.getMeetingNoteList(groupId)

    if(response.isSuccessful){
      val listData = response.body()!!.data!!.apply {
        this.forEach {
          it.groupId = groupId
        }
      }
      groupDao.insertMeetingNote(listData)
    }
  }

  suspend fun loadNoteByNo(groupId: String,noteNumber: Int){
    val response = groupClient.getMeetingNote(groupId, noteNumber)

    if(response.isSuccessful){
      groupDao.insertMeetingNote(response.body()!!.data!!)
    }
  }

  suspend fun postMeetingNote(groupId: String, notePostRequest: NotePostRequest): NotePostResponse {
    val response = groupClient.postMeetingNote(groupId, notePostRequest)

    val noteResponse = response.body()!!.data!!
    val meetingNote = groupDao.getMeetingNote(groupId, notePostRequest.meetingNumber)

    if(response.isSuccessful){
      meetingNote.note = notePostRequest.note
      meetingNote.lastUpdate = noteResponse.currentLastUpdate
      groupDao.insertMeetingNote(meetingNote)
    }

    return noteResponse
  }

}