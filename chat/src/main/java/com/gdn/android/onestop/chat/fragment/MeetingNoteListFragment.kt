package com.gdn.android.onestop.chat.fragment

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.gdn.android.onestop.base.BaseFullScreenFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.data.GroupDao
import com.gdn.android.onestop.chat.data.MeetingNote
import com.gdn.android.onestop.chat.databinding.FragmentNoteListBinding
import com.gdn.android.onestop.chat.injection.ChatComponent
import com.gdn.android.onestop.chat.util.MeetingNoteRecyclerAdapter
import com.gdn.android.onestop.chat.viewmodel.MeetingNoteListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MeetingNoteListFragment : BaseFullScreenFragment<FragmentNoteListBinding>(){

  override fun doFragmentInjection() {
    ChatComponent.getInstance().inject(this)
  }

  @Inject
  lateinit var viewmodel: MeetingNoteListViewModel

  @Inject
  lateinit var viewModelProvierFactory : ViewModelProviderFactory

  @Inject
  lateinit var groupDao: GroupDao

  lateinit var noteListLiveData: LiveData<List<MeetingNote>>

  private lateinit var group: Group
  private var scrollTargetNote: MeetingNote? = null

  private lateinit var noteListAdapter: MeetingNoteRecyclerAdapter

  private val notesItemEditClick = object : ItemClickCallback<MeetingNote>{
    override fun onItemClick(item: MeetingNote, position: Int) {
      findNavController().navigate(
        MeetingNoteListFragmentDirections.actionMeetingNoteListFragmentToMeetingNoteFragment(item.id, group.name)
      )
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val args : MeetingNoteListFragmentArgs by  navArgs()

    group = args.group

    viewmodel = ViewModelProvider(this, viewModelProvierFactory).get(MeetingNoteListViewModel::class.java)


    viewmodel.launch {
      viewmodel.loadMeetingData(group.id)
      args.scrollTargetId?.let{
        scrollTargetNote = groupDao.getMeetingNoteById(it)
      }
    }


  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    databinding = FragmentNoteListBinding.inflate(inflater, container, false)
    noteListAdapter = MeetingNoteRecyclerAdapter()
    noteListAdapter.itemEditClickCallback = notesItemEditClick
    databinding.rvNotes.adapter = noteListAdapter

    noteListLiveData = viewmodel.getNoteListLiveData(group.id)
    noteListLiveData.observe(this, Observer { meetingList ->
      noteListAdapter.updateData(meetingList)
      if(scrollTargetNote!= null && noteListAdapter.itemCount != 0){
        val targetNote = scrollTargetNote!!
        viewmodel.launch {
          val offset: Int = (databinding.rvNotes.height * (0.4)).toInt()
          val layoutManager = databinding.rvNotes.layoutManager as LinearLayoutManager
          val position = meetingList.size - targetNote.meetingNumber

          layoutManager.scrollToPositionWithOffset(position, offset)

          val colorFrom = ResourcesCompat.getColor(resources, R.color.reply_color, null)
          val animator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, 0)
          animator.duration = 2000
          noteListAdapter.animationTask = MeetingNoteRecyclerAdapter.ItemAnimationTask(position, animator)
        }
        scrollTargetNote = null
      }

    })

    databinding.llToolbar.tvToolbarTitle.text = group.name

    databinding.llToolbar.ivToolbarBack.setOnClickListener {
      findNavController().navigateUp()
    }


    return databinding.root
  }

  override fun onDestroyView() {
    noteListLiveData.removeObservers(this)
    super.onDestroyView()
  }
}