package com.gdn.android.onestop.group.fragment

import android.R.attr.label
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemServiceName
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.CopyTextFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.*
import com.gdn.android.onestop.group.R
import com.gdn.android.onestop.group.data.*
import com.gdn.android.onestop.group.databinding.FragmentChatRoomBinding
import com.gdn.android.onestop.group.injection.GroupComponent
import com.gdn.android.onestop.group.util.ChatRecyclerAdapter
import com.gdn.android.onestop.group.util.GroupUtil
import com.gdn.android.onestop.group.viewmodel.GroupChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupChatFragment : BaseFragment<FragmentChatRoomBinding>(){

  override fun doFragmentInjection() {
    GroupComponent.getInstance().inject(this)
  }

  companion object {
    var instance: GroupChatFragment? = null
  }

  @Inject
  lateinit var groupDao: GroupDao

  @Inject
  lateinit var sessionManager: SessionManager

  @Inject
  lateinit var viewModelProviderFactory : ViewModelProviderFactory

  @Inject
  lateinit var viewmodel : GroupChatViewModel

  @Inject
  lateinit var groupClient: GroupClient

  lateinit var chatRvAdapter: ChatRecyclerAdapter

  val group: Group by lazy {
    val tmp: GroupChatFragmentArgs by navArgs()
    tmp.groupModel
  }

  lateinit var groupInfo: GroupInfo

  private val chatObserver = Observer<List<GroupChat>> {
    setChatReaded()
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
          //          chatRvAdapter.notifyDataSetChanged()
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

  private val toMeetingNoteClick = object : ItemClickCallback<GroupChat> {
    override fun onItemClick(item: GroupChat, position: Int) {
      findNavController().navigate(
        GroupChatFragmentDirections.toMeetingNoteListFragment(group, item.id)
      )
    }
  }

  private val onProfileClick: ItemClickCallback<String> = object: ItemClickCallback<String> {
    override fun onItemClick(item: String, position: Int) {
      val fragment: DialogFragment = Navigator.getFragment(Navigator.Destination.PROFILE_DIALOG_FRAGMENT) as DialogFragment
      val bundle = Bundle()
      bundle.putString(Navigator.Argument.PROFILE_USERNAME.key, item)
      fragment.arguments = bundle
      fragment.show(fragmentManager!!, "profile fragment")

    }
  }

  private val onMessageLongClick: ItemClickCallback<String> = object: ItemClickCallback<String> {
    override fun onItemClick(item: String, position: Int) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        CopyTextFragment {
          val clipboard: ClipboardManager =
            context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

          val clip: ClipData = ClipData.newPlainText(item, item)
          clipboard.setPrimaryClip(clip)
        }.show(fragmentManager!!, "")
      }
    }
  }

  fun setChatReaded(){
    viewmodel.launch {
      if(::groupInfo.isInitialized){
        val tmp = groupDao.getGroupInfo(group.id)
        tmp.unreadChat = 0
        groupDao.insertGroupInfo(tmp)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewmodel = ViewModelProvider(this, viewModelProviderFactory)
      .get(GroupChatViewModel::class.java)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    instance = this

    databinding = FragmentChatRoomBinding.inflate(inflater, container, false)
    databinding.viewmodel = viewmodel

    setupChatRecyclerView()
    setupToolbar()
    setupBottomLayout()
    setupOptionLayout()


    viewmodel.launch {
      groupInfo = groupDao.getGroupInfo(group.id)
      groupInfo.unreadChat = 0
      groupDao.insertGroupInfo(groupInfo)
    }

    return databinding.root
  }

  private fun setupToolbar(){
    databinding.ivMute.visibility = if(group.isMute)View.VISIBLE
    else View.GONE

    databinding.tvGroupName.text = group.name

    databinding.ivBack.setOnClickListener {
      activity!!.finish()
    }

    val optionClick = View.OnClickListener {
      val isVisible = databinding.llChatOption.visibility == View.VISIBLE
      databinding.llChatOption.visibility = if(isVisible){
        View.GONE
      }
      else{
        View.VISIBLE
      }
      databinding.llShadow.visibility = databinding.llChatOption.visibility
    }

    databinding.ivOption.setOnClickListener(optionClick)
    databinding.llShadow.setOnClickListener(optionClick)


  }

  private fun setupOptionLayout(){
    databinding.llMeetingList.setOnClickListener {
      findNavController().navigate(
        GroupChatFragmentDirections.toMeetingNoteListFragment(group, null)
      )
    }

    databinding.llMute.setOnClickListener {
      group.isMute = !group.isMute

      databinding.ivMute.visibility = if(group.isMute)View.VISIBLE
      else View.GONE

      GroupUtil.setSoundIcon(databinding.ivNotification, group.isMute)
      GroupUtil.setSoundIconLabel(databinding.tvNotification, group.isMute)

      CoroutineScope(Dispatchers.IO).launch {
        groupInfo.isMute = group.isMute
        groupDao.insertGroup(group)
        groupDao.insertGroupInfo(groupInfo)
      }
    }

    databinding.llMember.setOnClickListener {
      val fragment = GroupMemberFragment()
      fragment.arguments = GroupMemberFragmentArgs(group).toBundle()

      fragment.show(fragmentManager!!,"member fragment")
    }

  }

  private fun setupBottomLayout(){
    databinding.btnChatSend.setOnClickListener {
      viewmodel.launch(Dispatchers.IO) {
        viewmodel.sendChat()
      }
    }
    databinding.btnMeeting.setOnClickListener {
      MeetingCreateFragment(
        object : FragmentActionCallback<MeetingCreateData>{
          override fun onActionSuccess(data: MeetingCreateData) {
            viewmodel.sendMeetingSchedule(data.datetime, data.description)
          }
        }
      ).show(fragmentManager!!,null)
    }
  }

  override fun onDestroy() {
    instance = null
    super.onDestroy()
    chatLiveData.removeObserver(chatObserver)
  }


  private fun setupChatRecyclerView(){
    chatLiveData = viewmodel.resetStateAndGetLiveData(group.id)
    chatLiveData.observe(this, chatObserver)
    chatRvAdapter = ChatRecyclerAdapter(onProfileClick, onMessageLongClick)
    chatRvAdapter.meetingNoteClickCallback = toMeetingNoteClick

    val point = Point()
    activity!!.windowManager.defaultDisplay.getSize(point)
    chatRvAdapter.layoutWidth = point.x

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

        val colorFrom = ResourcesCompat.getColor(resources, R.color.reply_color,null)
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

        if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
          databinding.cvDate.visibility = View.GONE
        }
        else{
          databinding.cvDate.visibility = View.VISIBLE
        }
      }

      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val item = chatRvAdapter.chatList[chatLayoutManager.findFirstVisibleItemPosition()]
        databinding.tvDate.text = item.createdAt.toDateString()
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