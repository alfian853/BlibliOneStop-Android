package com.gdn.android.onestop.chat.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gdn.android.onestop.chat.data.GroupDao
import com.gdn.android.onestop.chat.data.MeetingNote
import com.gdn.android.onestop.chat.data.MeetingNoteRepository
import com.gdn.android.onestop.chat.data.NotePostResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*

class MeetingNoteViewModelTest {

  @RelaxedMockK
  lateinit var groupDao: GroupDao

  @RelaxedMockK
  lateinit var meetingnoteRepository: MeetingNoteRepository

  lateinit var viewmodel: MeetingNoteViewModel

  private val mainThreadSurrogate = newSingleThreadContext("test-thread")

  private val NOTE_ID = "123"

  @get:Rule
  val testRule = InstantTaskExecutorRule()

  @Before
  fun setup(){
    Dispatchers.setMain(mainThreadSurrogate)
    MockKAnnotations.init(this)
    viewmodel = MeetingNoteViewModel(meetingnoteRepository, groupDao)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
    mainThreadSurrogate.close()
  }

  @Test
  fun submitNote_hasConflict(){
    val meetingNote = MeetingNote().apply {
      groupId = "asd"
    }

    coEvery { groupDao.getMeetingNoteById(any()) } returns meetingNote

    val note = "my note"
    viewmodel.noteText = note
    runBlocking { viewmodel.setMeetingNoteId(NOTE_ID) }

    val noteResponse = NotePostResponse(true, note, 10)

    coEvery { meetingnoteRepository.postMeetingNote(any(), any()) } returns noteResponse
    val result = runBlocking { viewmodel.submitNote() }
    Assert.assertFalse(result)
  }

  @Test
  fun submitNote_hasNoConflict(){
    val meetingNote = MeetingNote().apply {
      groupId = "asd"
    }

    coEvery { groupDao.getMeetingNoteById(any()) } returns meetingNote

    val note = "my note"
    viewmodel.noteText = note
    runBlocking { viewmodel.setMeetingNoteId(NOTE_ID) }

    val noteResponse = NotePostResponse(false, note, 10)

    coEvery { meetingnoteRepository.postMeetingNote(any(), any()) } returns noteResponse
    val result = runBlocking { viewmodel.submitNote() }
    Assert.assertTrue(result)
  }

}