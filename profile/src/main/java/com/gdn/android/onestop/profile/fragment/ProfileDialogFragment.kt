package com.gdn.android.onestop.profile.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.base.BaseFullScreenFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.*
import com.gdn.android.onestop.chat.ChatActivity
import com.gdn.android.onestop.chat.ChatActivityArgs
import com.gdn.android.onestop.chat.data.ChatDao
import com.gdn.android.onestop.chat.data.PersonalInfo
import com.gdn.android.onestop.chat.injection.ChatComponent
import com.gdn.android.onestop.chat.injection.ChatComponentProvider
import com.gdn.android.onestop.ideation.data.IdeaPost
import com.gdn.android.onestop.ideation.fragment.IdeaDetailFragment
import com.gdn.android.onestop.ideation.fragment.IdeaDetailFragmentArgs
import com.gdn.android.onestop.ideation.util.IdeaRecyclerAdapter
import com.gdn.android.onestop.ideation.util.VoteClickCallback
import com.gdn.android.onestop.ideation.util.VoteHelper
import com.gdn.android.onestop.profile.databinding.FragmentProfileBinding
import com.gdn.android.onestop.profile.injection.ProfileComponent
import com.gdn.android.onestop.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileDialogFragment : BaseFullScreenFragment<FragmentProfileBinding>() {

  override fun doFragmentInjection() {
    ProfileComponent.getInstance().inject(this)
    val chatComProvider = ChatComponentProvider()
    ChatComponent.getInstance().inject(chatComProvider)

    chatDao = chatComProvider.chatDao
  }

  @Inject
  lateinit var sessionManager: SessionManager

  @Inject
  lateinit var viewModelProviderFactory: ViewModelProviderFactory

  @Inject
  lateinit var voteHelper: VoteHelper

  lateinit var chatDao: ChatDao

  lateinit var profileViewModel: ProfileViewModel

  lateinit var username: String

  private val contextWrapper : DefaultContextWrapper by lazy {
    DefaultContextWrapper(
      this.context
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    profileViewModel = ViewModelProvider(this, viewModelProviderFactory).get(ProfileViewModel::class.java)

  }

  private fun showLoad(){
    databinding.user.detail.visibility = View.GONE
    databinding.pbLoad.visibility = View.VISIBLE
  }

  private fun hideLoad(){
    databinding.user.detail.visibility = View.VISIBLE
    databinding.pbLoad.visibility = View.GONE
    databinding.ivChat.visibility = View.VISIBLE
  }

  private fun loadUsername(): String {
    username = arguments!!.getString(Navigator.Argument.PROFILE_USERNAME.key, null)

    databinding.tvUser.text = username.toAliasName()
    databinding.tvUser.setBackgroundColor(Util.getColorFromString(username))
    databinding.tvUsername.text = username
    databinding.toolbar.tvToolbarTitle.text = username
    return username
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {

    databinding = FragmentProfileBinding.inflate(inflater, container, false)

    databinding.toolbar.ivToolbarBack.setOnClickListener {
      fragmentManager!!.beginTransaction().remove(this).commit()
    }

    showLoad()
    loadProfile()

    databinding.ivChat.setOnClickListener {
      val personalInfo = PersonalInfo().apply {
        id = username
        this.name = username
      }
      profileViewModel.launch {
        chatDao.insertPersonalInfo(personalInfo)

        val intent = Intent(this@ProfileDialogFragment.context, ChatActivity::class.java)
        intent.putExtras(ChatActivityArgs(null, personalInfo).toBundle())
        startActivity(intent)
      }
    }

    return databinding.root
  }

  private fun clickVote(ideaPost: IdeaPost,
    item: IdeaRecyclerAdapter.IdeaViewHolder,
    isVoteUp: Boolean){
    profileViewModel.launch {
      voteHelper.clickVote(item.tvUpVote, item.tvDownVote, contextWrapper, ideaPost, isVoteUp)
    }
  }

  private fun loadProfile(){
    profileViewModel.launch {
      val username = loadUsername()

      val profile = profileViewModel.getProfile(username) ?: return@launch

      databinding.user.tvPoint.text = profile.points.toString()
      databinding.user.tvReadedBook.text = profile.readedBooks.toString() + " books"
      databinding.user.tvListenedAudio.text = profile.listenedAudios.toString() + " audios"
      databinding.user.tvIdeationPosts.text = profile.ideationPosts.toString() + " posts"
      databinding.user.tvIdeationComments.text = profile.ideationComments.toString() + " comments"
      databinding.user.tvWritenNote.text = profile.writtenMeetingNotes.toString() + " notes"

      val adapter = IdeaRecyclerAdapter(voteHelper).apply {
        voteClickCallback = object : VoteClickCallback {
          override fun onVote(
            ideaPost: IdeaPost,
            item: IdeaRecyclerAdapter.IdeaViewHolder,
            isVoteUp: Boolean)
          {
            clickVote(ideaPost, item, isVoteUp)
          }
        }

        itemContentClickCallback = object : ItemClickCallback<IdeaPost> {
          override fun onItemClick(item: IdeaPost, position: Int) {
            val args = IdeaDetailFragmentArgs(item)
            val fm : FragmentManager = this@ProfileDialogFragment.fragmentManager!!
            val ideaDetailFragment = IdeaDetailFragment()
            ideaDetailFragment.arguments = args.toBundle()
            ideaDetailFragment.show(fm,"detail fragment")
          }
        }

        hideLoad()
      }

      adapter.updateData(profile.topIdeas)

      databinding.user.rvTopIdea.adapter = adapter
    }

  }


}