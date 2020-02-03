package com.gdn.android.onestop.chat.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.base.BaseFullScreenFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.chat.ChatActivityArgs
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.databinding.FragmentGroupCreateBinding
import com.gdn.android.onestop.chat.injection.ChatComponent
import com.gdn.android.onestop.chat.viewmodel.ChatListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class GroupCreateFragment : BaseFullScreenFragment<FragmentGroupCreateBinding>() {

    override fun doFragmentInjection() {
        ChatComponent.getInstance().inject(this)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory

    lateinit var viewModel : ChatListViewModel

    private val ERROR_GROUP_NAME = "Please fill name name"

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
            .get(ChatListViewModel::class.java)


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
                        val arg = ChatActivityArgs(it, null)

                        val intent = Intent(activity, com.gdn.android.onestop.chat.ChatActivity::class.java)
                        intent.putExtras(arg.toBundle())
                        startActivity(intent)
                    }
                }
            }
            else{
                databinding.tilGroupName.error = ERROR_GROUP_NAME
            }

        }

        return databinding.root
    }
}