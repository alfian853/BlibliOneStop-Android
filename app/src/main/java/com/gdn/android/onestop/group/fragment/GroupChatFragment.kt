package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.gdn.android.onestop.base.BaseFullSceenFragment
import com.gdn.android.onestop.databinding.FragmentChatRoomBinding
import com.gdn.android.onestop.group.data.Group


class GroupChatFragment : BaseFullSceenFragment<FragmentChatRoomBinding>(){

    val group : Group by lazy {
        val tmp : GroupChatFragmentArgs by navArgs()
        tmp.groupModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = FragmentChatRoomBinding.inflate(inflater, container, false)

        databinding.tvGroupName.text = group.name

        return databinding.root
    }
}