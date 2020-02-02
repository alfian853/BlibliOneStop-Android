package com.gdn.android.onestop.chat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.gdn.android.onestop.chat.databinding.FragmentPdGroupLeaveBinding
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.viewmodel.GroupViewModel
import com.gdn.android.onestop.base.util.ActionSuccessCallback
import kotlinx.coroutines.launch


class GroupLeaveFragment
    constructor(
        private val groupViewModel: GroupViewModel,
        private val group : Group,
        private val leaveGroupCallback : ActionSuccessCallback?
    ) : DialogFragment(){

    lateinit var dataBinding: FragmentPdGroupLeaveBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentPdGroupLeaveBinding.inflate(inflater, container, false)
        dataBinding.groupName = group.name

        dataBinding.btnCancel.setOnClickListener {
            fragmentManager!!.beginTransaction().remove(this).commit()
        }

        dataBinding.btnLeave.setOnClickListener {
            groupViewModel.launch {
                groupViewModel.leaveGroup(group.id)
                Toast.makeText(this@GroupLeaveFragment.context, "You leave the group", Toast.LENGTH_SHORT).show()
                leaveGroupCallback?.onSuccess()
                fragmentManager!!.beginTransaction().remove(this@GroupLeaveFragment).commit()

            }

        }

        return dataBinding.root
    }
}