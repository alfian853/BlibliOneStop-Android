package com.gdn.android.onestop.ideation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.DefaultContextWrapper
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.NetworkUtil
import com.gdn.android.onestop.ideation.data.IdeaPost
import com.gdn.android.onestop.ideation.databinding.FragmentIdeaChannelBinding
import com.gdn.android.onestop.ideation.injection.IdeaComponent
import com.gdn.android.onestop.ideation.util.IdeaRecyclerAdapter
import com.gdn.android.onestop.ideation.util.VoteClickCallback
import com.gdn.android.onestop.ideation.util.VoteHelper
import com.gdn.android.onestop.ideation.viewmodel.IdeaChannelViewModel
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var networkUtil: NetworkUtil

    lateinit var viewmodel: IdeaChannelViewModel

    private val contextWrapper : DefaultContextWrapper by lazy {
        DefaultContextWrapper(
            this.context
        )
    }

    private val observer = Observer<PagedList<IdeaPost>> {
        Log.d("idea","on submit list ${it.size}")
        this.ideaRecyclerAdapter.submitList(it)
        databinding.swipeLayout.isRefreshing = false
        context?.let {context ->
            if(it.isEmpty() && !networkUtil.isConnectedToNetwork()){
                Toast.makeText(context,
                    NO_CONNECTION, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val liveData : LiveData<PagedList<IdeaPost>> by lazy { viewmodel.getIdeaLiveData() }

    override fun doFragmentInjection() {
        IdeaComponent.getInstance().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this, viewModelProviderFactory)
            .get(IdeaChannelViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        liveData.observe(this, observer)
        this.databinding = FragmentIdeaChannelBinding.inflate(inflater,container,false)
        this.databinding.rvIdeapost.adapter = this.ideaRecyclerAdapter
        this.databinding.swipeLayout.isRefreshing = true

        this.databinding.swipeLayout.isRefreshing = false
        this.databinding.swipeLayout.setOnRefreshListener {
            viewmodel.viewModelScope.launch {
                viewmodel.refreshIdeaChannelData()
                databinding.swipeLayout.isRefreshing = false
            }
        }


        ideaRecyclerAdapter.itemContentClickCallback = object :
            ItemClickCallback<IdeaPost> {
            override fun onItemClick(item: IdeaPost, position: Int) {
                val args = IdeaDetailFragmentArgs(item)
                val fm : FragmentManager = this@IdeaChannelFragment.fragmentManager!!
                val ideaDetailFragment = IdeaDetailFragment()
                ideaDetailFragment.arguments = args.toBundle()
                ideaDetailFragment.show(fm,"detail fragment")
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

        databinding.rvIdeapost.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(!recyclerView.canScrollVertically(1)){
                    viewmodel.viewModelScope.launch {
                        databinding.pbLoadmore.visibility = View.VISIBLE
                        viewmodel.loadMoreData()
                        databinding.pbLoadmore.visibility = View.GONE
                    }
                }
            }
        })

        databinding.etIdea.setOnClickListener {
            val fm : FragmentManager = this@IdeaChannelFragment.fragmentManager!!
            val ideaCreateFragment = IdeaCreateFragment()
            ideaCreateFragment.show(fm,"idea create fragment")
        }

        return databinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
//        liveData.removeObserver(observer)

    }

    private fun clickVote(ideaPost: IdeaPost,
                          item: IdeaRecyclerAdapter.IdeaViewHolder,
                          isVoteUp: Boolean){
        voteHelper.clickVote(item.tvUpVote, item.tvDownVote, contextWrapper, ideaPost, isVoteUp)
    }


}