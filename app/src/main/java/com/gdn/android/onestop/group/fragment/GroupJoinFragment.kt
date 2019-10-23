package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import com.gdn.android.onestop.databinding.FragmentPdGroupJoinBinding
import com.gdn.android.onestop.group.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

class GroupJoinFragment(private val groupViewModel: GroupViewModel) : DialogFragment() {

    // TODO add input validation

    lateinit var databinding : FragmentPdGroupJoinBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = FragmentPdGroupJoinBinding.inflate(inflater, container, false)

        databinding.btnCancel.setOnClickListener {
            this.fragmentManager!!.beginTransaction().remove(this).commit()
        }

        databinding.btnJoin.setOnClickListener {
            groupViewModel.viewModelScope.launch {
                val group = groupViewModel.joinGroup(databinding.etCode.text.toString())

                fragmentManager!!.beginTransaction().remove(this@GroupJoinFragment).commit()

                val chatFragment = GroupChatFragment()
                val args = GroupChatFragmentArgs(group!!)
                chatFragment.arguments = args.toBundle()
                chatFragment.show(
                    fragmentManager!!, "group chat fragment"
                )
            }
        }
        return databinding.root
    }
}