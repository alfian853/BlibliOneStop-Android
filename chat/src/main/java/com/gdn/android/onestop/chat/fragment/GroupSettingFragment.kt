package com.gdn.android.onestop.chat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.viewmodel.ChatListViewModel
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.data.GroupDao
import com.gdn.android.onestop.chat.databinding.FragmentBsGroupSettingBinding
import com.gdn.android.onestop.chat.injection.ChatComponent
import com.gdn.android.onestop.chat.util.GroupUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupSettingFragment(
  private val groupViewModel: ChatListViewModel,
  private val group: Group
) : BottomSheetDialogFragment() {

  lateinit var binding: FragmentBsGroupSettingBinding

  @Inject
  lateinit var groupDao: GroupDao

  override fun onCreate(savedInstanceState: Bundle?) {
    ChatComponent.getInstance().inject(this)
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentBsGroupSettingBinding.inflate(inflater, container, false)
    binding.groupName = group.name
    binding.groupCode = group.groupCode


    binding.llLeave.setOnClickListener {
      AlertDialog.Builder(this.context!!)
        .setTitle(R.string.leave_group)
        .setMessage(R.string.are_you_sure)
        .setPositiveButton(R.string.yes) { dialog, which ->
          groupViewModel.launch {
            groupViewModel.leaveGroup(group.id)
            Toast.makeText(this@GroupSettingFragment.context, "You leave the name", Toast.LENGTH_SHORT).show()
            fragmentManager!!.beginTransaction().remove(this@GroupSettingFragment).commit()
          }
        }.setNegativeButton(R.string.no) { dialog, which -> }.show()
    }


    GroupUtil.setSoundIcon(binding.ivMute, group.isMute)
    GroupUtil.setSoundIconLabel(binding.tvMute, group.isMute)

    binding.llMute.setOnClickListener {
      group.isMute = !group.isMute

      GroupUtil.setSoundIcon(binding.ivMute, group.isMute)
      GroupUtil.setSoundIconLabel(binding.tvMute, group.isMute)

      CoroutineScope(Dispatchers.IO).launch {
        val groupInfo = groupDao.getGroupInfo(group.id)
        groupInfo.isMute = group.isMute
        groupDao.insertGroup(group)
        groupDao.insertGroupInfo(groupInfo)
      }

    }

    binding.llMembers.setOnClickListener {
      val fragment = GroupMemberFragment()
      fragment.arguments = GroupMemberFragmentArgs(group).toBundle()

      fragment.show(fragmentManager!!,"member fragment")
    }


    return binding.root
  }
}