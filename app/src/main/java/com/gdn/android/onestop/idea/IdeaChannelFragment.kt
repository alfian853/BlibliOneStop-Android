package com.gdn.android.onestop.idea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import com.gdn.android.onestop.R
import com.gdn.android.onestop.app.ViewModelProviderFactory
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.ItemClickCallback
import com.gdn.android.onestop.base.VoteClickCallback
import com.gdn.android.onestop.databinding.FragmentIdeaChannelBinding
import com.gdn.android.onestop.idea.data.IdeaPost
import com.gdn.android.onestop.util.NetworkUtil
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class IdeaChannelFragment : BaseFragment<FragmentIdeaChannelBinding>() {

    companion object {
        private const val NO_CONNECTION = "No Connection..."
    }

    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    @Inject
    lateinit var ideaRecyclerViewAdapter: IdeaRecyclerViewAdapter

    lateinit var ideaViewModel: IdeaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.let {
            this.ideaViewModel = ViewModelProvider(it, viewModelProviderFactory)
                .get(IdeaViewModel::class.java)
        }
        this.databinding = FragmentIdeaChannelBinding.inflate(inflater,container,false)
        this.databinding.rvIdeapost.adapter = this.ideaRecyclerViewAdapter
        this.databinding.swipeLayout.isRefreshing = true
        this.databinding.swipeLayout.setOnRefreshListener {
            ideaViewModel.reloadData().subscribeOn(Schedulers.newThread()).subscribe{isSuccess ->
                this.databinding.swipeLayout.isRefreshing = false
                if(!isSuccess){
                    this@IdeaChannelFragment.activity?.runOnUiThread{
                        Toast.makeText(context, NO_CONNECTION,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        val liveData : LiveData<PagedList<IdeaPost>> = ideaViewModel.getIdeaLiveData()


        liveData.observe(this, Observer<PagedList<IdeaPost>> {
            this.ideaRecyclerViewAdapter.submitList(it)
            databinding.swipeLayout.isRefreshing = false
            context?.let {context ->
                if(it.isEmpty() && !NetworkUtil.isConnectedToNetwork(context)){
                    Toast.makeText(context, NO_CONNECTION, Toast.LENGTH_SHORT).show()
                }
            }
        })


        ideaRecyclerViewAdapter.itemContentClickCallback = object : ItemClickCallback<IdeaPost> {
            override fun onItemClick(item: IdeaPost, position: Int) {
                val args = IdeaDetailFragmentArgs(position)
                findNavController().navigate(R.id.to_detail, args.toBundle())
            }
        }

        ideaRecyclerViewAdapter.voteClickCallback = object : VoteClickCallback {
            override fun onVote(
                ideaPost: IdeaPost,
                itemView: IdeaRecyclerViewAdapter.IdeaViewHolder,
                isVoteUp: Boolean)
            {
                clickVote(ideaPost, itemView, isVoteUp)

            }
        }

        return databinding.root
    }



}