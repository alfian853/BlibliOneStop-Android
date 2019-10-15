package com.gdn.android.onestop.idea

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.gdn.android.onestop.util.DefaultContextWrapper
import com.gdn.android.onestop.util.ItemClickCallback
import com.gdn.android.onestop.util.VoteClickCallback
import com.gdn.android.onestop.databinding.FragmentIdeaChannelBinding
import com.gdn.android.onestop.idea.data.IdeaPost
import com.gdn.android.onestop.login.LoginActivity
import com.gdn.android.onestop.util.NetworkUtil
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class IdeaChannelFragment : BaseFragment<FragmentIdeaChannelBinding>() {

    companion object {
        private const val NO_CONNECTION = "No Connection..."
    }

    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    @Inject
    lateinit var ideaRecyclerAdapter: IdeaRecyclerAdapter

    @Inject
    lateinit var voteHelper: VoteHelper

    lateinit var viewmodel: IdeaChannelViewModel

    private val contextWrapper : DefaultContextWrapper by lazy {
        DefaultContextWrapper(
            this.context
        )
    }

    private val observer = Observer<PagedList<IdeaPost>> {
        this.ideaRecyclerAdapter.submitList(it)
        databinding.swipeLayout.isRefreshing = false
        context?.let {context ->
            if(it.isEmpty() && !NetworkUtil.isConnectedToNetwork(context)){
                Toast.makeText(context, NO_CONNECTION, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val liveData : LiveData<PagedList<IdeaPost>> by lazy { viewmodel.getIdeaLiveData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this, viewModelProviderFactory)
            .get(IdeaChannelViewModel::class.java)
        viewmodel.context = context!!
        liveData.observe(this, observer)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.databinding = FragmentIdeaChannelBinding.inflate(inflater,container,false)
        this.databinding.rvIdeapost.adapter = this.ideaRecyclerAdapter
        this.databinding.swipeLayout.isRefreshing = true
        this.databinding.swipeLayout.setOnRefreshListener {
            viewmodel.refreshIdeaChannelData().subscribeOn(Schedulers.io()).subscribe{ isSuccess ->
                this.databinding.swipeLayout.isRefreshing = false
                if(!isSuccess){
                    this@IdeaChannelFragment.activity?.runOnUiThread{
                        Toast.makeText(context, NO_CONNECTION,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        ideaRecyclerAdapter.itemContentClickCallback = object :
            ItemClickCallback<IdeaPost> {
            override fun onItemClick(item: IdeaPost, position: Int) {
                val args = IdeaDetailFragmentArgs(item)
                findNavController().navigate(R.id.to_detail, args.toBundle())
            }
        }

        ideaRecyclerAdapter.voteClickCallback = object :
            VoteClickCallback {
            override fun onVote(
                ideaPost: IdeaPost,
                item: IdeaRecyclerAdapter.IdeaViewHolder,
                isVoteUp: Boolean)
            {
                clickVote(ideaPost, item, isVoteUp)
            }
        }

        return databinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        liveData.removeObserver(observer)
    }

    private fun clickVote(ideaPost: IdeaPost,
                  item: IdeaRecyclerAdapter.IdeaViewHolder,
                  isVoteUp: Boolean){
        voteHelper.clickVote(item.tvUpVote, item.tvDownVote, contextWrapper, ideaPost, isVoteUp)
    }


}