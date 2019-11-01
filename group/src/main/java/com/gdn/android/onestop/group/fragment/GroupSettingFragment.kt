package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.viewmodel.GroupViewModel
import com.gdn.android.onestop.base.util.ActionSuccessCallback
import com.gdn.android.onestop.group.databinding.FragmentBsGroupSettingBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GroupSettingFragment(private val groupViewModel: GroupViewModel,
                           private val group : Group
) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBsGroupSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBsGroupSettingBinding.inflate(inflater, container, false)
        binding.groupName = group.name
        binding.groupCode = group.groupCode

        val leaveGroupClickListener = View.OnClickListener {
            val groupLeaveFragment = GroupLeaveFragment(
                groupViewModel,
                group,
                object :
                    ActionSuccessCallback {
                    override fun onSuccess() {
                        fragmentManager!!.beginTransaction().remove(this@GroupSettingFragment)
                            .commit()
                    }
                })

            groupLeaveFragment.show(this@GroupSettingFragment.fragmentManager!!, "leave group fragment")
        }

        binding.ivLeave.setOnClickListener(leaveGroupClickListener)
        binding.tvLeave.setOnClickListener(leaveGroupClickListener)



        return binding.root
    }
}