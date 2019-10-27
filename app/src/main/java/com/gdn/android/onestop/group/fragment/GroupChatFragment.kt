package com.gdn.android.onestop.group.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.gdn.android.onestop.app.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseFullSceenFragment
import com.gdn.android.onestop.databinding.FragmentChatRoomBinding
import com.gdn.android.onestop.group.ChatService
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.group.data.GroupDao
import com.gdn.android.onestop.group.util.ChatRecyclerAdapter
import com.gdn.android.onestop.group.viewmodel.GroupChatViewModel
import com.gdn.android.onestop.util.SessionManager
import javax.inject.Inject


class GroupChatFragment : BaseFullSceenFragment<FragmentChatRoomBinding>(){

    @Inject
    lateinit var groupDao: GroupDao

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var viewModelProvierFactory : ViewModelProviderFactory

    @Inject
    lateinit var viewmodel : GroupChatViewModel

    private val chatRvAdapter = ChatRecyclerAdapter()

    val group : Group by lazy {
        val tmp : GroupChatFragmentArgs by navArgs()
        tmp.groupModel
    }

    val chatObserver = Observer<List<GroupChat>> {
        Log.d("chat-onestop","on update")
        chatRvAdapter.updateChatList(it)
        databinding.rvChat.scrollToPosition(chatRvAdapter.itemCount-1)
    }

    lateinit var chatLiveData : LiveData<List<GroupChat>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this, viewModelProvierFactory)
            .get(GroupChatViewModel::class.java)
        viewmodel.activeGroup = group
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = FragmentChatRoomBinding.inflate(inflater, container, false)
        chatLiveData = viewmodel.getChatLiveData(group.id)
        chatLiveData.observe(this, chatObserver)

        databinding.tvGroupName.text = group.name
        databinding.viewmodel = viewmodel
        databinding.rvChat.adapter = chatRvAdapter
        Log.d("chat-onestop","current thread : ${Thread.currentThread().name}")

        val intent = Intent(this.context, ChatService::class.java)
        this.context!!.startService(intent)

        databinding.ivBack.setOnClickListener {
            fragmentManager!!.beginTransaction().remove(this).commit()
        }

        return databinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        chatLiveData.removeObserver(chatObserver)
    }
}