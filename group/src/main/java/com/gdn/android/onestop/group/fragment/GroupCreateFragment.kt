package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gdn.android.onestop.group.R
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseFullSceenFragment
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.databinding.FragmentGroupCreateBinding
import com.gdn.android.onestop.group.injection.GroupComponent
import com.gdn.android.onestop.group.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupCreateFragment : BaseFullSceenFragment<FragmentGroupCreateBinding>() {

    override fun doFragmentInjection() {
        GroupComponent.getInstance().inject(this)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory

    lateinit var viewModel : GroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        databinding = FragmentGroupCreateBinding.inflate(inflater, container, false)

        databinding.ivBack.setOnClickListener {
            this.fragmentManager!!.beginTransaction().remove(this).commit()
        }

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(GroupViewModel::class.java)


        databinding.ivCreate.setOnClickListener {

            if(databinding.etGroupName.text!!.isNotEmpty()){
                val groupType = when(databinding.rgGroupType.checkedRadioButtonId){
                    R.id.ritem_tribe -> Group.Type.TRIBE
                    R.id.ritem_squad -> Group.Type.SQUAD
                    else -> Group.Type.GUILD
                }

                val groupName = databinding.etGroupName.text.toString()

                viewModel.viewModelScope.launch {
                    val group: Group? = viewModel.createGroup(groupName, groupType)
                    Log.d("idea",group.toString())

                    fragmentManager!!.beginTransaction().remove(this@GroupCreateFragment).commit()

                    val chatFragment = GroupChatFragment()
                    val args = GroupChatFragmentArgs(group!!)
                    chatFragment.arguments = args.toBundle()
                    chatFragment.show(
                        fragmentManager!!, "group chat fragment"
                    )

                }
            }
            else{
                Toast.makeText(this.context, "please fill", Toast.LENGTH_SHORT).show()
            }

        }

        return databinding.root
    }
}