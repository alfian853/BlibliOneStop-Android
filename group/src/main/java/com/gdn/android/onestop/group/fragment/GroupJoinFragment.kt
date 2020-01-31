package com.gdn.android.onestop.group.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.gdn.android.onestop.base.BaseDialogFragment
import com.gdn.android.onestop.group.GroupActivity
import com.gdn.android.onestop.group.GroupActivityArgs
import com.gdn.android.onestop.group.databinding.FragmentPdGroupJoinBinding
import com.gdn.android.onestop.group.viewmodel.GroupViewModel
import kotlinx.coroutines.launch

class GroupJoinFragment(private val groupViewModel: GroupViewModel) : BaseDialogFragment<FragmentPdGroupJoinBinding>() {

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
                    val arg = GroupActivityArgs(group)

                    val intent = Intent(activity, GroupActivity::class.java)
                    intent.putExtras(arg.toBundle())
                    startActivity(intent)
                }

            }
        }
        return databinding.root
    }
}