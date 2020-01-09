package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.group.databinding.FragmentNoteBinding
import com.gdn.android.onestop.group.injection.GroupComponent
import com.gdn.android.onestop.group.viewmodel.MeetingNoteViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MeetingNoteFragment : BaseFragment<FragmentNoteBinding>(){
  override fun doFragmentInjection() {
    GroupComponent.getInstance().inject(this)
  }

  lateinit var viewmodel: MeetingNoteViewModel

  @Inject
  lateinit var viewModelProvierFactory : ViewModelProviderFactory

  private val args: MeetingNoteFragmentArgs by navArgs()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    viewmodel = ViewModelProvider(this, viewModelProvierFactory).get(MeetingNoteViewModel::class.java)
    viewmodel.launch {
      viewmodel.setMeetingNoteId(args.meetingNoteId)
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    databinding = FragmentNoteBinding.inflate(inflater, container, false)
    databinding.viewmodel = viewmodel
    databinding.btnIdeaSubmit.setOnClickListener {
      viewmodel.launch {
        Toast.makeText(this@MeetingNoteFragment.context,"Please wait",Toast.LENGTH_SHORT).show()
        val isDone = viewmodel.submitNote()
        if(isDone){
          Toast.makeText(this@MeetingNoteFragment.context,"Note Saved",Toast.LENGTH_SHORT).show()
        }
        else{
          Toast.makeText(
            this@MeetingNoteFragment.context,"There is conflict, please edit for final content",
            Toast.LENGTH_LONG
          ).show()
        }
      }
    }


    databinding.llToolbar.ivToolbarBack.setOnClickListener {
      findNavController().navigateUp()
    }

    return databinding.root
  }
}