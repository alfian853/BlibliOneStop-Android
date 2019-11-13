package com.gdn.android.onestop.group.fragment

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.group.databinding.FragmentChatRoomBinding
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseFullScreenFragment
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.group.util.ChatRecyclerAdapter
import com.gdn.android.onestop.group.viewmodel.GroupChatViewModel
import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.base.util.Util
import com.gdn.android.onestop.group.R
import com.gdn.android.onestop.group.data.*
import com.gdn.android.onestop.group.injection.GroupComponent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class GroupChatFragment : BaseFullScreenFragment<FragmentChatRoomBinding>(){

    override fun doFragmentInjection() {
        GroupComponent.getInstance().inject(this)
    }

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

    private var chatRvAdapter = ChatRecyclerAdapter()

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

    @Inject
    lateinit var groupChatRepository: GroupChatRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewModelProvider(this, viewModelProvierFactory)
            .get(GroupChatViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        databinding = FragmentChatRoomBinding.inflate(inflater, container, false)
        chatLiveData = viewmodel.resetStateAndGetLiveData(group.id)
        chatLiveData.observe(this, chatObserver)
        chatRvAdapter = ChatRecyclerAdapter()
        databinding.tvGroupName.text = group.name
        databinding.viewmodel = viewmodel
        val point = Point()
        activity!!.windowManager.defaultDisplay.getSize(point)
        chatRvAdapter.layoutWidth = point.x

        databinding.ivBack.setOnClickListener {
            fragmentManager!!.beginTransaction().remove(this).commit()
        }

        databinding.btnChatSend.setOnClickListener {
            GlobalScope.launch {
                Log.d("chat","send")
                viewmodel.sendChat()
                Log.d("chat","complete")
            }
        }

        setupChatRecyclerView()

        return databinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        chatLiveData.removeObserver(chatObserver)
    }


    private fun setupChatRecyclerView(){
        val chatLayoutManager = LinearLayoutManager(this.context)

        // init reply click callback
        val onReplyClickCallback = object: ItemClickCallback<GroupChat>{
            override fun onItemClick(item: GroupChat, position: Int) {
                val chatList = chatRvAdapter.chatList

                var repliedPosition = position
                for(i in position-1 downTo 0){
                    if(chatList[i].id == item.repliedId){
                        repliedPosition = i
                        break
                    }
                }

                val offset : Int = (databinding.rvChat.height*(0.3)).toInt()
                chatLayoutManager.scrollToPositionWithOffset(repliedPosition, offset)

                val colorFrom = ResourcesCompat.getColor(resources,R.color.reply_color,null)
                val animator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, 0)
                animator.duration = 2000

                val target = chatRvAdapter.itemViewArray[repliedPosition]
                if(target == null){
                    chatRvAdapter.pendingReplyShowAnimation = ChatRecyclerAdapter.ItemAnimationTask(repliedPosition, animator)
                }
                else{
                    animator.addUpdateListener {
                        target.setBackgroundColor(it.animatedValue as Int)
                    }
                    animator.start()
                }
            }

        }

        chatRvAdapter.repliedClickCallback = onReplyClickCallback

        databinding.rvChat.adapter = chatRvAdapter
        databinding.rvChat.layoutManager = chatLayoutManager

        databinding.rvChat.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(!recyclerView.canScrollVertically(-1)){
                    viewmodel.loadMoreChatBefore()
                }
            }
        })

        // handle reply
        databinding.clReplyContainer.visibility = View.GONE
        databinding.ivReplyClose.setOnClickListener {
            viewmodel.setOffReplyChat()
        }

        val itemSwipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                chatRvAdapter.notifyItemChanged(position)
                val chat = chatRvAdapter.chatList[position]
                if(chat.isSending)return
                val previewText = Util.shrinkText(chat.text)
                viewmodel.setOnReplyChat(chat.id, chat.username, previewText)

                databinding.tvReplyUsername.text = chat.username
                databinding.tvReplyUserPict.text = chat.nameAlias
                databinding.tvReplyUserPict.setBackgroundColor(chat.nameColor)
                databinding.tvReplyUsername.setTextColor(chat.nameColor)
                databinding.tvReplyMessage.text = Util.shrinkText(previewText)
                databinding.clReplyContainer.visibility = View.VISIBLE
            }

        }
        ItemTouchHelper(itemSwipeCallback).attachToRecyclerView(databinding.rvChat)

    }
}