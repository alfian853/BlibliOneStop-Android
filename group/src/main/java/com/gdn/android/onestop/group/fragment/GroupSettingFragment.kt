package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.viewmodel.GroupViewModel
import com.gdn.android.onestop.base.util.ActionSuccessCallback
import com.gdn.android.onestop.group.R
import com.gdn.android.onestop.group.data.GroupDao
import com.gdn.android.onestop.group.databinding.FragmentBsGroupSettingBinding
import com.gdn.android.onestop.group.injection.GroupComponent
import com.gdn.android.onestop.group.util.GroupUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupSettingFragment(
  private val groupViewModel: GroupViewModel,
  private val group: Group
) : BottomSheetDialogFragment() {

  lateinit var binding: FragmentBsGroupSettingBinding

  @Inject
  lateinit var groupDao: GroupDao

  override fun onCreate(savedInstanceState: Bundle?) {
    GroupComponent.getInstance().inject(this)
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
      val groupLeaveFragment = GroupLeaveFragment(
        groupViewModel, group, object : ActionSuccessCallback {
          override fun onSuccess() {
            fragmentManager!!.beginTransaction().remove(this@GroupSettingFragment).commit()
          }
        })

      groupLeaveFragment.show(this@GroupSettingFragment.fragmentManager!!, "leave group fragment")
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


    return binding.root
  }
}