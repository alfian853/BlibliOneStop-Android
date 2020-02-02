package com.gdn.android.onestop.chat.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gdn.android.onestop.base.BaseDialogFragment
import com.gdn.android.onestop.chat.ChatActivityArgs
import com.gdn.android.onestop.chat.databinding.FragmentPdGroupJoinBinding
import com.gdn.android.onestop.chat.viewmodel.ChatListViewModel
import kotlinx.coroutines.launch

class GroupJoinFragment(private val groupViewModel: ChatListViewModel) : BaseDialogFragment<FragmentPdGroupJoinBinding>() {

    private val ERROR_WRONG_GROUP_CODE = "Invalid Group Code"

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
            groupViewModel.launch {
                val group = groupViewModel.joinGroup(databinding.etCode.text.toString())

                fragmentManager!!.beginTransaction().remove(this@GroupJoinFragment).commit()

                if(group == null){
                    Toast.makeText(context, ERROR_WRONG_GROUP_CODE, Toast.LENGTH_SHORT).show()
                }
                else{
                    val arg = ChatActivityArgs(group, null)

                    val intent = Intent(activity, com.gdn.android.onestop.chat.ChatActivity::class.java)
                    intent.putExtras(arg.toBundle())
                    startActivity(intent)
                }

            }
        }
        return databinding.root
    }
}