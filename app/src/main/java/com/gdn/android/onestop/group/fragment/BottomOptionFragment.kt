package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdn.android.onestop.databinding.FragmentBsGroupBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomOptionFragment : BottomSheetDialogFragment(){


    lateinit var binding: FragmentBsGroupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBsGroupBinding.inflate(inflater, container, false)

        val createGroupClickListener = View.OnClickListener {
            val createGroupFragment = CreateGroupFragment()
            createGroupFragment.show(
                this.fragmentManager!!, "create group fragment"
            )
        }

        binding.ivCreateGroup.setOnClickListener(createGroupClickListener)
        binding.tvCreateGroup.setOnClickListener(createGroupClickListener)

        return binding.root
    }
}