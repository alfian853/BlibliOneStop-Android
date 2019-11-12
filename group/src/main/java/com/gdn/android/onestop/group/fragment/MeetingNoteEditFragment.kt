package com.gdn.android.onestop.group.fragment

import com.gdn.android.onestop.base.BaseFullScreenFragment
import com.gdn.android.onestop.group.databinding.FragmentNoteBinding
import com.gdn.android.onestop.group.injection.GroupComponent

class MeetingNoteEditFragment : BaseFullScreenFragment<FragmentNoteBinding>(){
  override fun doFragmentInjection() {
    GroupComponent.getInstance().inject(this)
  }
}