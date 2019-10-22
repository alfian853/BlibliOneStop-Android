package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdn.android.onestop.base.BaseDialogFragment
import com.gdn.android.onestop.databinding.FragmentChatRoomBinding


class GroupChatFragment : BaseDialogFragment<FragmentChatRoomBinding>(){


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = FragmentChatRoomBinding.inflate(inflater, container, false)



        return databinding.root
    }
}