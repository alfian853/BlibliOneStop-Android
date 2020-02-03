package com.gdn.android.onestop.chat.fragment

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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
import com.gdn.android.onestop.base.Constant
import com.gdn.android.onestop.base.CopyTextFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.*
import com.gdn.android.onestop.chat.ChatActivityArgs
import com.gdn.android.onestop.chat.data.GroupChat
import com.gdn.android.onestop.chat.data.GroupChatRepository
import com.gdn.android.onestop.chat.service.MeetingAlarmPublisher
import com.gdn.android.onestop.chat.util.GroupChatRecyclerAdapter
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.data.*
import com.gdn.android.onestop.chat.databinding.FragmentGroupChatBinding
import com.gdn.android.onestop.chat.injection.ChatComponent
import com.gdn.android.onestop.chat.util.BaseChatRecyclerAdapter
import com.gdn.android.onestop.chat.util.GroupUtil
import com.gdn.android.onestop.chat.viewmodel.GroupChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


class GroupChatFragment : BaseFragment<FragmentGroupChatBinding>(){

  override fun doFragmentInjection() {
    ChatComponent.getInstance().inject(this)
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
  lateinit var groupRepository: GroupRepository

  lateinit var chatRvAdapter: GroupChatRecyclerAdapter

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
          val difSize = it.size-oldSize
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

  private val toMeetingNoteClick = object : ItemClickCallback<GroupChat> {
    override fun onItemClick(item: GroupChat, position: Int) {
      findNavController().navigate(
        GroupChatFragmentDirections.toMeetingNoteListFragment(
          group, item.id
        )
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

    databinding = FragmentGroupChatBinding.inflate(inflater, container, false)
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

    databinding.llLeave.setOnClickListener {
      AlertDialog.Builder(this.context!!)
        .setTitle(R.string.leave_group)
        .setMessage(R.string.are_you_sure)
        .setPositiveButton(R.string.yes) { dialog, which ->
          viewmodel.launch {
            groupRepository.leaveGroup(group.id)
            activity!!.finish()
          }
        }.setNegativeButton(R.string.no) { dialog, which -> }.show()
    }

  }

  private fun getMeetingNotification(meetingDateTime: Long) : Notification{
    val mainIntent = Intent(context, com.gdn.android.onestop.chat.ChatActivity::class.java)
    mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    mainIntent.putExtras(ChatActivityArgs(group, null).toBundle())

    val mainPIntent: PendingIntent = PendingIntent.getActivity(
      context, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT
    )


    return NotificationCompat.Builder(context!!, Constant.NOTIF_CHAT_CHANNEL_ID)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setSmallIcon(R.drawable.ic_group_thin)
      .setColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
      .setContentTitle(group.name)
      .setContentIntent(mainPIntent)
      .setContentText("You have a meeting at ${meetingDateTime.toTime24String()} today")
      .setAutoCancel(true)
      .build()
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
            viewmodel.launch {
              val isSuccess = viewmodel.sendMeetingSchedule(data.datetime, data.description)
              if(isSuccess){
                val context = this@GroupChatFragment.context!!
                val notification = getMeetingNotification(data.datetime)

                val notificationIntent = Intent( this@GroupChatFragment.context, MeetingAlarmPublisher::class.java)
                notificationIntent.putExtra(Constant.NOTIF_MEETING_CHANNEL_ID , notification)
                val serviceIntent = PendingIntent.getBroadcast(context, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT )
                val alarmManager = context.getSystemService(Context. ALARM_SERVICE ) as AlarmManager
                alarmManager.set(AlarmManager.RTC_WAKEUP , data.datetime - Constant.HOURS_TO_MS, serviceIntent)
              }
            }
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
    val chatLayoutManager = LinearLayoutManager(this.context)

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
          chatRvAdapter.pendingReplyShowAnimation = BaseChatRecyclerAdapter.ItemAnimationTask(repliedPosition, animator)
        }
        else{
          animator.addUpdateListener {
            target.setBackgroundColor(it.animatedValue as Int)
          }
          animator.start()
        }
      }

    }
    chatLiveData = viewmodel.resetStateAndGetLiveData(group.id)
    chatLiveData.observe(this, chatObserver)
    chatRvAdapter = GroupChatRecyclerAdapter(onProfileClick, onMessageLongClick, onReplyClickCallback, toMeetingNoteClick)

    val point = Point()
    activity!!.windowManager.defaultDisplay.getSize(point)
    chatRvAdapter.layoutWidth = point.x

    databinding.rvChat.adapter = chatRvAdapter
    databinding.rvChat.layoutManager = chatLayoutManager
    databinding.cvDate.visibility = View.GONE
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
        try {
          val item = chatRvAdapter.chatList[chatLayoutManager.findFirstVisibleItemPosition()]
          databinding.tvDate.text = item.createdAt.toDateString()
        }
        catch (e: Exception){

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