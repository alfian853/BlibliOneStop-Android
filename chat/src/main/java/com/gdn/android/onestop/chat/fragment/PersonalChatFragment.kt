package com.gdn.android.onestop.chat.fragment

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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.CopyTextFragment
import com.gdn.android.onestop.base.User
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.*
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.data.*
import com.gdn.android.onestop.chat.databinding.FragmentPersonalChatBinding
import com.gdn.android.onestop.chat.injection.ChatComponent
import com.gdn.android.onestop.chat.util.BaseChatRecyclerAdapter
import com.gdn.android.onestop.chat.util.GroupUtil
import com.gdn.android.onestop.chat.viewmodel.PersonalChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


class PersonalChatFragment : BaseFragment<FragmentPersonalChatBinding>(){

  override fun doFragmentInjection() {
    ChatComponent.getInstance().inject(this)
  }

  companion object {
    var instance: PersonalChatFragment? = null
  }

  @Inject
  lateinit var chatDao: ChatDao

  @Inject
  lateinit var sessionManager: SessionManager

  val myUser: User by lazy {sessionManager.user!!}

  @Inject
  lateinit var viewModelProviderFactory : ViewModelProviderFactory

  @Inject
  lateinit var viewmodel : PersonalChatViewModel

  lateinit var chatRvAdapter: BaseChatRecyclerAdapter<PersonalChat>

  private val personalInfo: PersonalInfo by lazy {
    val tmp: PersonalChatFragmentArgs by navArgs()
    tmp.personalInfo
  }


  private val chatObserver = Observer<List<PersonalChat>> {
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
          chatRvAdapter.notifyItemRangeInserted(0,difSize-1)
        }
        else{
          chatRvAdapter.chatList = it
          chatRvAdapter.notifyDataSetChanged()
          databinding.rvChat.scrollToPosition(it.size-1)
        }
      }
    }
    else{
      chatRvAdapter.chatList = it
      chatRvAdapter.notifyDataSetChanged()
    }
  }

  lateinit var chatLiveData : LiveData<List<PersonalChat>>

  @Inject
  lateinit var groupChatRepository: PersonalChatRepository


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
      chatDao.resetUnreadedPersonalChat(personalInfo.name)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewmodel = ViewModelProvider(this, viewModelProviderFactory)
      .get(PersonalChatViewModel::class.java)

    setChatReaded()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    instance = this

    databinding = FragmentPersonalChatBinding.inflate(inflater, container, false)
    databinding.viewmodel = viewmodel

    setupChatRecyclerView()
    setupToolbar()
    setupBottomLayout()
    setupOptionLayout()


    return databinding.root
  }

  private fun setupToolbar(){
    viewmodel.launch {
      databinding.ivMute.visibility = if(personalInfo.isMute)View.VISIBLE
      else View.GONE
    }

    databinding.tvGroupName.text = personalInfo.name

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

    databinding.llMute.setOnClickListener {
      personalInfo.isMute = !personalInfo.isMute

      databinding.ivMute.visibility = if(personalInfo.isMute)View.VISIBLE
      else View.GONE

      GroupUtil.setSoundIcon(databinding.ivNotification, personalInfo.isMute)
      GroupUtil.setSoundIconLabel(databinding.tvNotification, personalInfo.isMute)

      CoroutineScope(Dispatchers.IO).launch {
        chatDao.insertPersonalInfo(personalInfo)
      }
    }

    databinding.llRemoveChat.setOnClickListener {
      AlertDialog.Builder(this.context!!)
        .setTitle(getString(R.string.remove_chat))
        .setMessage(R.string.are_you_sure)
        .setPositiveButton(R.string.yes) { dialog, which ->
          viewmodel.launch {
            chatDao.deletePersonalChat(personalInfo.name)
          }
        }.setNegativeButton(R.string.no) { dialog, which -> }.show()
    }

  }

  private fun setupBottomLayout(){
    databinding.btnChatSend.setOnClickListener {
      viewmodel.launch(Dispatchers.IO) {
        viewmodel.sendChat()
      }
    }
  }

  override fun onDestroy() {
    instance = null
    super.onDestroy()
    chatLiveData.removeObserver(chatObserver)
  }


  private fun setupChatRecyclerView(){
    val chatLayoutManager = LinearLayoutManager(this.context)

    val onReplyClickCallback = object: ItemClickCallback<PersonalChat>{
      override fun onItemClick(item: PersonalChat, position: Int) {
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
    chatLiveData = viewmodel.resetStateAndGetLiveData(personalInfo.name)
    chatLiveData.observe(this, chatObserver)
    chatRvAdapter = BaseChatRecyclerAdapter(onProfileClick, onMessageLongClick, onReplyClickCallback)

    val point = Point()
    activity!!.windowManager.defaultDisplay.getSize(point)
    chatRvAdapter.layoutWidth = point.x

    databinding.rvChat.adapter = chatRvAdapter
    databinding.rvChat.layoutManager = chatLayoutManager
    databinding.cvDate.visibility = View.GONE
    databinding.rvChat.addOnScrollListener(object: RecyclerView.OnScrollListener(){

      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//        if(!recyclerView.canScrollVertically(-1)){
//          viewmodel.loadMoreChatBefore()
//        }
//
//        if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
//          databinding.cvDate.visibility = View.GONE
//        }
//        else{
//          databinding.cvDate.visibility = View.VISIBLE
//        }
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

        val repliedUsername = if(chat.isMe){
          myUser.username
        }
        else {
          personalInfo.name
        }
        viewmodel.setOnReplyChat(chat.id, previewText, repliedUsername)

        if(chat.isMe){
          databinding.tvReplyUsername.text = myUser.username
          databinding.tvReplyUserPict.text = myUser.alias
        }
        else{
          databinding.tvReplyUsername.text = chat.getSenderName()
          databinding.tvReplyUserPict.text = chat.alias
        }

        databinding.tvReplyUserPict.setBackgroundColor(chat.nameColor)
        databinding.tvReplyUsername.setTextColor(chat.nameColor)
        databinding.tvReplyMessage.text = Util.shrinkText(previewText)
        databinding.clReplyContainer.visibility = View.VISIBLE
      }

    }
    ItemTouchHelper(itemSwipeCallback).attachToRecyclerView(databinding.rvChat)
  }

}