package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gdn.android.onestop.R
import com.gdn.android.onestop.app.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseDialogFragment
import com.gdn.android.onestop.databinding.FragmentCreateGroupBinding
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateGroupFragment : BaseDialogFragment<FragmentCreateGroupBinding>() {


    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory

    lateinit var viewModel : GroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        databinding = FragmentCreateGroupBinding.inflate(inflater, container, false)

        databinding.ivBack.setOnClickListener {
            this.fragmentManager!!.beginTransaction().remove(this).commit()
        }

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(GroupViewModel::class.java)


        databinding.ivCreate.setOnClickListener {

            val groupType = when(databinding.rgGroupType.checkedRadioButtonId){
                R.id.ritem_tribe -> Group.Type.GUILD
                R.id.ritem_squad -> Group.Type.SQUAD
                else -> Group.Type.GUILD
            }

            val groupName = databinding.etGroupName.text.toString()

            viewModel.viewModelScope.launch {
                val group: Group? = viewModel.createGroup(groupName, groupType)
                Log.d("idea",group.toString())
                //move to chat room
            }


        }

        return databinding.root
    }
}