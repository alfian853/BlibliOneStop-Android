package com.gdn.android.onestop.ideation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gdn.android.onestop.base.BaseFullScreenFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.*
import com.gdn.android.onestop.ideation.R
import com.gdn.android.onestop.ideation.data.IdeaComment
import com.gdn.android.onestop.ideation.data.IdeaPost
import com.gdn.android.onestop.ideation.databinding.FragmentIdeaDetailBinding
import com.gdn.android.onestop.ideation.injection.IdeaComponent
import com.gdn.android.onestop.ideation.util.IdeaCommentRecyclerAdapter
import com.gdn.android.onestop.ideation.util.VoteHelper
import com.gdn.android.onestop.ideation.viewmodel.IdeaDetailViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class IdeaDetailFragment : BaseFullScreenFragment<FragmentIdeaDetailBinding>(){

    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    @Inject
    lateinit var voteHelper: VoteHelper

    lateinit var viewmodel: IdeaDetailViewModel

    lateinit var ideaPost : IdeaPost

    lateinit var commentLiveData: LiveData<List<IdeaComment>>

    private val profileClickCallback: ItemClickCallback<String> = object: ItemClickCallback<String> {
        override fun onItemClick(item: String, position: Int) {
            val fragment: DialogFragment = Navigator.getFragment(Navigator.Destination.PROFILE_DIALOG_FRAGMENT) as DialogFragment
            val bundle = Bundle()
            bundle.putString(Navigator.Argument.PROFILE_USERNAME.key, item)
            fragment.arguments = bundle
            fragment.show(fragmentManager!!, "profile fragment")

        }
    }

    private val ideaCommentRecyclerAdapter: IdeaCommentRecyclerAdapter = IdeaCommentRecyclerAdapter(profileClickCallback)

    private val commentObserver: Observer<List<IdeaComment>> = Observer {
        Log.d("updatehttp",it.size.toString())
        ideaCommentRecyclerAdapter.updateData(it)
    }

    private val contextWrapper : DefaultContextWrapper by lazy {
        DefaultContextWrapper(
            this.context
        )
    }

    override fun doFragmentInjection() {
        IdeaComponent.getInstance()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ideaCommentRecyclerAdapter.updateData(emptyList())
        viewmodel = ViewModelProvider(this, viewModelProviderFactory).get(IdeaDetailViewModel::class.java)
        viewmodel.contextWrapper = contextWrapper

        viewmodel.reportLiveData.observe(this,Observer<String> {
            contextWrapper.toast(it)
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val args : IdeaDetailFragmentArgs by navArgs()
        ideaPost = args.ideaPost

        databinding = FragmentIdeaDetailBinding.inflate(inflater,container, false)
        databinding.toolbar.tvToolbarTitle.text = ideaPost.username

        databinding.toolbar.ivToolbarBack.setOnClickListener {
            fragmentManager?.beginTransaction()?.remove(this@IdeaDetailFragment)?.commit()
        }

        databinding.tvUser.text = ideaPost.username.toAliasName()
        databinding.tvUser.setBackgroundColor(Util.getColorFromString(ideaPost.username))

        databinding.viewmodel = viewmodel

        viewmodel.setIdeaPostId(ideaPost.id)
        databinding.rvComment.adapter = ideaCommentRecyclerAdapter

        commentLiveData = viewmodel.getCommentsLiveData()
        commentLiveData.observe(this, commentObserver)
        viewmodel.loadMoreComment()

        databinding.tvUsername.text = ideaPost.username
        databinding.tvContent.text = ideaPost.content
        databinding.tvDate.text = ideaPost.createdAt.toDateTime24String()
        databinding.tvComment.text = ("${resources.getString(R.string.fa_comment)} ${ideaPost.commentCount}")

        databinding.etCommentInput.doOnTextChanged { text, _, _, _ ->

            if(text == null || text.isEmpty()){
                databinding.btnCommentSubmit.setTextColor(
                    ResourcesCompat.getColor(resources, R.color.faded_blue, null)
                )
            }
            else{
                databinding.btnCommentSubmit.setTextColor(
                    ResourcesCompat.getColor(resources, R.color.blue, null)
                )
            }
        }

        databinding.btnCommentSubmit.setOnClickListener {
            viewmodel.submitComment {
                ideaPost.commentCount++
                databinding.tvComment.text =
                    ("${resources.getString(R.string.fa_comment)} ${ideaPost.commentCount}")
                databinding.scrollView.fullScroll(View.FOCUS_DOWN)
            }
        }

        this.databinding.tvUpVote.setOnClickListener {
            clickVote(ideaPost ,true)
        }

        this.databinding.tvDownVote.setOnClickListener {
            clickVote(ideaPost ,false)
        }

        setVoteText()

        return this.databinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel.getCommentsLiveData().removeObserver(commentObserver)
    }

    private fun setVoteText(){
        voteHelper.setVoteText(databinding.tvUpVote, resources, true, ideaPost.upVoteCount, ideaPost.isMeVoteUp)
        voteHelper.setVoteText(databinding.tvDownVote, resources, false, ideaPost.downVoteCount, ideaPost.isMeVoteDown)
    }

    private fun clickVote(ideaPost: IdeaPost, isVoteUp: Boolean){
        viewmodel.launch {
            voteHelper.clickVote(
                databinding.tvUpVote,
                databinding.tvDownVote,
                contextWrapper,
                ideaPost,
                isVoteUp
            )
        }
    }

}