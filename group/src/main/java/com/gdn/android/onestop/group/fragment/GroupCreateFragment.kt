package com.gdn.android.onestop.group.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.group.R
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseFullScreenFragment
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.group.GroupActivity
import com.gdn.android.onestop.group.GroupActivityArgs
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.databinding.FragmentGroupCreateBinding
import com.gdn.android.onestop.group.injection.GroupComponent
import com.gdn.android.onestop.group.viewmodel.GroupViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class GroupCreateFragment : BaseFullScreenFragment<FragmentGroupCreateBinding>() {

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

        databinding.etGroupName.setText("")
        databinding.ivBack.setOnClickListener {
            this.fragmentManager!!.beginTransaction().remove(this).commit()
        }

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(GroupViewModel::class.java)


        databinding.etGroupName.doOnTextChanged { text, start, count, after ->
            if(text != null && text.isNotEmpty()){
                databinding.tilGroupName.error = null
            }
        }

        databinding.ivCreate.setOnClickListener {

            if(Util.isNotEmpty(databinding.etGroupName.text)){
                val groupType = when(databinding.rgGroupType.checkedRadioButtonId){
                    R.id.ritem_tribe -> Group.Type.TRIBE
                    R.id.ritem_squad -> Group.Type.SQUAD
                    else -> Group.Type.GUILD
                }

                val groupName = databinding.etGroupName.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val group: Group? = viewModel.createGroup(groupName, groupType)

                    fragmentManager!!.beginTransaction().remove(this@GroupCreateFragment).commit()

                    group?.let {
                        val arg = GroupActivityArgs(it)

                        val intent = Intent(activity, GroupActivity::class.java)
                        intent.putExtras(arg.toBundle())
                        startActivity(intent)
                    }
                }
            }
            else{
                databinding.tilGroupName.error = "Please fill group name"
            }

        }

        return databinding.root
    }
}