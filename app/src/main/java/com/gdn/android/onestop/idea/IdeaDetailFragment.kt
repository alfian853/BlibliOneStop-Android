package com.gdn.android.onestop.idea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gdn.android.onestop.R
import com.gdn.android.onestop.app.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.databinding.FragmentIdeaDetailBinding
import com.gdn.android.onestop.idea.data.IdeaPost
import javax.inject.Inject

class IdeaDetailFragment : BaseFragment<FragmentIdeaDetailBinding>(){

    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    lateinit var ideaViewModel : IdeaViewModel

    val ideaPost : IdeaPost by lazy {
        val args : IdeaDetailFragmentArgs by navArgs()
        val ideaIndex = args.itemPosition

        return@lazy ideaViewModel.getPostAt(ideaIndex)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        this.databinding = FragmentIdeaDetailBinding.inflate(inflater,container, false)
        activity?.let {
            ideaViewModel = ViewModelProvider(it, viewModelProviderFactory).get(IdeaViewModel::class.java)
            this.databinding.viewmodel = ideaViewModel
        }

        databinding.tvUsername.text = ideaPost.username
        databinding.tvContent.text = ideaPost.content
        databinding.tvDate.text = ideaPost.createdAt
        Glide.with(this@IdeaDetailFragment)
            .load(R.drawable.ic_iconfinder_male_628288)
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
}