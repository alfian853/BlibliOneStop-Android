package com.gdn.android.onestop.group.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.gdn.android.onestop.base.BaseFullScreenFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.Navigator
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.databinding.FragmentMemberListBinding
import com.gdn.android.onestop.group.injection.GroupComponent
import com.gdn.android.onestop.group.util.MemberRecyclerAdapter
import com.gdn.android.onestop.group.viewmodel.GroupMemberViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupMemberFragment : BaseFullScreenFragment<FragmentMemberListBinding>() {

  override fun doFragmentInjection() {
    GroupComponent.getInstance().inject(this)
  }

  val group: Group by lazy {
    val args: GroupMemberFragmentArgs by navArgs()
    args.group
  }

  @Inject
  lateinit var viewModelProviderFactory: ViewModelProviderFactory

  lateinit var groupMemberViewModel: GroupMemberViewModel

  val adapter = MemberRecyclerAdapter(object : ItemClickCallback<String> {
    override fun onItemClick(item: String, position: Int) {
      val fragment: DialogFragment = Navigator.getFragment(Navigator.Destination.PROFILE_DIALOG_FRAGMENT) as DialogFragment
      val bundle = Bundle()
      bundle.putString(Navigator.Argument.PROFILE_USERNAME.key ,item)
      fragment.arguments = bundle
      fragment.show(fragmentManager!!, "profile fragment")
    }
  })

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    groupMemberViewModel = ViewModelProvider(this, viewModelProviderFactory).get(GroupMemberViewModel::class.java)

    groupMemberViewModel.launch {
      groupMemberViewModel.fetchMember(group.id)
    }

    groupMemberViewModel.membersLiveData.observe(this, Observer {
      adapter.updateData(it)
    })
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    databinding = FragmentMemberListBinding.inflate(inflater, container, false)

    databinding.toolbar.tvToolbarTitle.text = group.name

    databinding.toolbar.ivToolbarBack.setOnClickListener {
      fragmentManager!!.beginTransaction().remove(this).commit()
    }

    databinding.rvMembers.adapter = adapter


    return databinding.root
  }
}