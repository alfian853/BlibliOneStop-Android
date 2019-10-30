package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gdn.android.onestop.app.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseFullSceenFragment
import com.gdn.android.onestop.databinding.FragmentChatRoomBinding
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.data.GroupChat
import com.gdn.android.onestop.group.data.GroupClient
import com.gdn.android.onestop.group.data.GroupDao
import com.gdn.android.onestop.group.util.ChatRecyclerAdapter
import com.gdn.android.onestop.group.viewmodel.GroupChatViewModel
import com.gdn.android.onestop.util.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var groupClient: GroupClient

    private val chatRvAdapter = ChatRecyclerAdapter()

    val group : Group by lazy {
        val tmp : GroupChatFragmentArgs by navArgs()
        tmp.groupModel
    }

    private val chatObserver = Observer<List<GroupChat>> {
        if(it.isNotEmpty()){
            if(chatRvAdapter.chatList.isEmpty()){
                chatRvAdapter.updateChatList(it)
                databinding.rvChat.scrollToPosition(it.size-1)
            }
            else{
                // if load old chat
                if(chatRvAdapter.chatList[0].createdAt > it[0].createdAt){
                    val oldSize = chatRvAdapter.chatList.size
                    val difSize = it.size-oldSize-1
                    chatRvAdapter.chatList = it
                    chatRvAdapter.notifyItemRangeInserted(0,difSize)
                }
                else{
                    chatRvAdapter.chatList = it
                    chatRvAdapter.notifyDataSetChanged()
                    databinding.rvChat.scrollToPosition(it.size-1)
                }
            }

        }
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
        chatLiveData = viewmodel.getChatLiveData()
        chatLiveData.observe(this, chatObserver)

        databinding.tvGroupName.text = group.name
        databinding.viewmodel = viewmodel
        databinding.rvChat.adapter = chatRvAdapter


        databinding.ivBack.setOnClickListener {
            fragmentManager!!.beginTransaction().remove(this).commit()
        }

        databinding.rvChat.setOnScrollChangeListener { v, _, _, _, _ ->
            if(!v.canScrollVertically(-1)){
                viewmodel.loadMoreChatBefore()
            }
        }

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result!!.token

                viewmodel.viewModelScope.launch {
                    groupClient.subscribeGroupsByToken(token)
                }
            })


        databinding.btnChatSend.setOnClickListener {
            viewmodel.viewModelScope.launch {
                viewmodel.sendChat()
            }
        }

        return databinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        chatLiveData.removeObserver(chatObserver)
    }
}