package com.gdn.android.onestop.group.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.group.ChatActivity
import com.gdn.android.onestop.group.ChatActivityArgs
import com.gdn.android.onestop.group.data.GroupDao
import com.gdn.android.onestop.group.data.GroupMeeting
import com.gdn.android.onestop.group.databinding.FragmentMeetingListBinding
import com.gdn.android.onestop.group.injection.GroupComponent
import com.gdn.android.onestop.group.util.MeetingListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


class MeetingListFragment : BaseFragment<FragmentMeetingListBinding>() {

  override fun doFragmentInjection() {
    GroupComponent.getInstance().inject(this)
  }

  @Inject
  lateinit var groupDao: GroupDao

  private val itemClickCallback = object : ItemClickCallback<GroupMeeting> {
    override fun onItemClick(item: GroupMeeting, position: Int) {
      CoroutineScope(Dispatchers.Main).launch {
        val group = groupDao.getGroupById(item.groupId)
        val arg = ChatActivityArgs(group)

        val intent = Intent(activity, ChatActivity::class.java)
        intent.putExtras(arg.toBundle())
        startActivity(intent)
      }
    }
  }

  private val meetingRvAdapter = MeetingListAdapter().apply {
    this.itemClickCallback = this@MeetingListFragment.itemClickCallback
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    databinding = FragmentMeetingListBinding.inflate(inflater, container, false)

    groupDao.getAllNextMeeting(Date().time).observe(this,Observer {
      if(it.isEmpty()){
        databinding.llNoMeeting.visibility = View.VISIBLE
      }
      else{
        databinding.llNoMeeting.visibility = View.GONE
      }
      meetingRvAdapter.updateMeetingList(it)
    })

    databinding.rvMeeting.adapter = meetingRvAdapter



    return databinding.root
  }
}