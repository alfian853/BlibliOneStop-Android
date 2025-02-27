package com.gdn.android.onestop.chat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdn.android.onestop.chat.databinding.FragmentBsGroupOptionBinding
import com.gdn.android.onestop.chat.viewmodel.ChatListViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GroupOptionFragment(private val groupViewModel: ChatListViewModel) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBsGroupOptionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBsGroupOptionBinding.inflate(inflater, container, false)

        val createGroupClickListener = View.OnClickListener {
            val createGroupFragment = GroupCreateFragment()
            createGroupFragment.show(
                this.fragmentManager!!, "create name fragment"
            )
        }
        val joinGroupClickListener = View.OnClickListener {
            val groupJoinFragment =
                GroupJoinFragment(groupViewModel)

            groupJoinFragment.show(
                this.fragmentManager!!,"name join fragment"
            )
        }

        binding.ivCreateGroup.setOnClickListener(createGroupClickListener)
        binding.tvCreateGroup.setOnClickListener(createGroupClickListener)

        binding.ivJoin.setOnClickListener(joinGroupClickListener)
        binding.tvJoin.setOnClickListener(joinGroupClickListener)

        return binding.root
    }
}