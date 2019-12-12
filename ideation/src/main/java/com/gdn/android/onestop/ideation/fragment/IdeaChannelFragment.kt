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
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.User
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.DefaultContextWrapper
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.NetworkUtil
import com.gdn.android.onestop.base.util.SessionManager
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

    @Inject
    lateinit var sessionManager : SessionManager

    private val user : User by lazy {
        sessionManager.user!!
    }

    lateinit var viewmodel: IdeaChannelViewModel

    private val contextWrapper : DefaultContextWrapper by lazy {
        DefaultContextWrapper(
            this.context
        )
    }

    private var isLoading = false

    private fun showLoading(){
        databinding.pbLoadmore.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        databinding.pbLoadmore.visibility = View.GONE
    }

    private val observer = Observer<List<IdeaPost>> {
        ideaRecyclerAdapter.ideaList = it
        ideaRecyclerAdapter.notifyDataSetChanged()
        databinding.swipeLayout.isRefreshing = false
        context?.let {context ->
            if(it.isEmpty() && !networkUtil.isConnectedToNetwork()){
                Toast.makeText(context,
                    NO_CONNECTION, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val liveData : LiveData<List<IdeaPost>> by lazy { viewmodel.getIdeaLiveData() }

    override fun doFragmentInjection() {
        IdeaComponent.getInstance().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this, viewModelProviderFactory)
            .get(IdeaChannelViewModel::class.java)

        isLoading = false
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
            if(!isLoading){
                viewmodel.launch {
                    showLoading()
                    isLoading = true
                    viewmodel.refreshIdeaChannelData()
                    databinding.swipeLayout.isRefreshing = false
                    isLoading = false
                }
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
                    if(!isLoading){
                        isLoading = true
                        viewmodel.launch {
                            val isLast = viewmodel.loadMoreData()
                            isLoading = false
                            if(isLast){
                                hideLoading()
                            }
                        }
                    }
                }
            }
        })

        databinding.etIdea.setOnClickListener {
            val fm : FragmentManager = this@IdeaChannelFragment.fragmentManager!!
            val ideaCreateFragment = IdeaCreateFragment()
            ideaCreateFragment.show(fm,"idea create fragment")
        }

        databinding.tvUser.text = user.alias
        databinding.tvUser.setBackgroundColor(user.color)

        return databinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        liveData.removeObserver(observer)

    }

    private fun clickVote(ideaPost: IdeaPost,
                          item: IdeaRecyclerAdapter.IdeaViewHolder,
                          isVoteUp: Boolean){
        voteHelper.clickVote(item.tvUpVote, item.tvDownVote, contextWrapper, ideaPost, isVoteUp)
    }


}