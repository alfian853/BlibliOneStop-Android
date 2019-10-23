package com.gdn.android.onestop.idea.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.paging.PagedList
import com.bumptech.glide.Glide
import com.gdn.android.onestop.R
import com.gdn.android.onestop.app.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseFullSceenFragment
import com.gdn.android.onestop.databinding.FragmentIdeaDetailBinding
import com.gdn.android.onestop.idea.util.IdeaCommentRecyclerAdapter
import com.gdn.android.onestop.idea.viewmodel.IdeaDetailViewModel
import com.gdn.android.onestop.idea.util.VoteHelper
import com.gdn.android.onestop.idea.data.IdeaComment
import com.gdn.android.onestop.idea.data.IdeaPost
import com.gdn.android.onestop.util.DefaultContextWrapper
import javax.inject.Inject


class IdeaDetailFragment : BaseFullSceenFragment<FragmentIdeaDetailBinding>{

    constructor() : super(){
        Log.d(TAG,"create new fragment")
    }

    companion object{
        private const val TAG = "ideaDetailFragment"
    }

    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    @Inject
    lateinit var ideaCommentRecyclerAdapter: IdeaCommentRecyclerAdapter

    @Inject
    lateinit var voteHelper: VoteHelper

    lateinit var viewmodel: IdeaDetailViewModel

    lateinit var ideaPost : IdeaPost

    lateinit var commentLiveData: LiveData<PagedList<IdeaComment>>

    private val commentObserver: Observer<PagedList<IdeaComment>> = Observer {
        ideaCommentRecyclerAdapter.submitList(it)
    }

    private val contextWrapper : DefaultContextWrapper by lazy {
        DefaultContextWrapper(
            this.context
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        ideaCommentRecyclerAdapter.currentList?.clear()

        val args : IdeaDetailFragmentArgs by navArgs()
        ideaPost = args.ideaPost

        databinding = FragmentIdeaDetailBinding.inflate(inflater,container, false)
        databinding.viewmodel = viewmodel

        viewmodel.setIdeaPostId(ideaPost.id)
        databinding.rvComment.adapter = ideaCommentRecyclerAdapter

        commentLiveData = viewmodel.getPagedCommentLiveData()
        commentLiveData.observe(this, commentObserver)
        viewmodel.loadMoreComment()

        databinding.tvUsername.text = ideaPost.username
        databinding.tvContent.text = ideaPost.content
        databinding.tvDate.text = ideaPost.createdAt
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

        Glide.with(this@IdeaDetailFragment)
            .load(R.drawable.ic_default_user)
            .into(databinding.ivUser)


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
        viewmodel.getPagedCommentLiveData().removeObserver(commentObserver)
    }

    private fun setVoteText(){
        voteHelper.setVoteText(databinding.tvUpVote, resources, true, ideaPost.upVoteCount, ideaPost.isMeVoteUp)
        voteHelper.setVoteText(databinding.tvDownVote, resources, false, ideaPost.downVoteCount, ideaPost.isMeVoteDown)
    }

    private fun clickVote(ideaPost: IdeaPost, isVoteUp: Boolean){
        voteHelper.clickVote(databinding.tvUpVote, databinding.tvDownVote, contextWrapper, ideaPost, isVoteUp)
    }

}