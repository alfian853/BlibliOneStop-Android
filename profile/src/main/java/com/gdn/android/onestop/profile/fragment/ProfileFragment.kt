package com.gdn.android.onestop.profile.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.User
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.*
import com.gdn.android.onestop.ideation.data.IdeaPost
import com.gdn.android.onestop.ideation.fragment.IdeaDetailFragment
import com.gdn.android.onestop.ideation.fragment.IdeaDetailFragmentArgs
import com.gdn.android.onestop.ideation.util.IdeaRecyclerAdapter
import com.gdn.android.onestop.ideation.util.VoteClickCallback
import com.gdn.android.onestop.ideation.util.VoteHelper
import com.gdn.android.onestop.profile.R
import com.gdn.android.onestop.profile.databinding.FragmentProfileBinding
import com.gdn.android.onestop.profile.injection.ProfileComponent
import com.gdn.android.onestop.profile.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

  override fun doFragmentInjection() {
    ProfileComponent.getInstance().inject(this)
  }

  @Inject
  lateinit var sessionManager: SessionManager

  @Inject
  lateinit var viewModelProviderFactory: ViewModelProviderFactory

  @Inject
  lateinit var voteHelper: VoteHelper

  lateinit var profileViewModel: ProfileViewModel

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
  }

  private fun loadUsername(): String {

    val user = sessionManager.user!!

    databinding.tvUser.text = user.alias
    databinding.tvUser.setBackgroundColor(user.color)
    databinding.tvUsername.text = user.username

    return user.username
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    setHasOptionsMenu(true)

    databinding = FragmentProfileBinding.inflate(inflater, container, false)
    databinding.toolbar.toolbar.visibility = View.GONE

    showLoad()
    loadProfile()

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
            val fm : FragmentManager = this@ProfileFragment.fragmentManager!!
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

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.profile_menu, menu)
    menu.findItem(R.id.logout).setOnMenuItemClickListener {
      AlertDialog.Builder(this.context!!)
        .setTitle(R.string.logout)
        .setMessage(R.string.are_you_sure)
        .setPositiveButton(R.string.yes) { dialog, which ->
          sessionManager.logout()
          val loginIntent = Navigator.getIntent(Navigator.Destination.LOGIN_ACTIVITY)
          startActivity(loginIntent)
          activity!!.finish()
        }
        .setNegativeButton(R.string.no) { dialog, which -> }.show()
      true
    }
  }
}