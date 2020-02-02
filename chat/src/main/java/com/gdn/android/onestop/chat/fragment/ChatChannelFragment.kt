package com.gdn.android.onestop.chat.fragment

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.chat.ChatActivityArgs
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.data.ChatChannel
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.data.GroupDao
import com.gdn.android.onestop.chat.data.PersonalInfo
import com.gdn.android.onestop.chat.databinding.FragmentChatListBinding
import com.gdn.android.onestop.chat.injection.ChatComponent
import com.gdn.android.onestop.chat.util.ChatChannelRecyclerAdapter
import com.gdn.android.onestop.chat.viewmodel.ChatListViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatChannelFragment : BaseFragment<FragmentChatListBinding>() {

    override fun doFragmentInjection() {
        ChatComponent.getInstance().inject(this)
    }

    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    @Inject
    lateinit var viewModel: ChatListViewModel

    @Inject
    lateinit var groupDao: GroupDao

    private val groupClickCallback = object : ItemClickCallback<ChatChannel> {
        override fun onItemClick(item: ChatChannel, position: Int) {
            val arg = ChatActivityArgs(item as Group,null)

            val intent = Intent(activity, com.gdn.android.onestop.chat.ChatActivity::class.java)
            intent.putExtras(arg.toBundle())
            startActivity(intent)
        }
    }

    private val groupOptionClick = object : ItemClickCallback<ChatChannel> {
        override fun onItemClick(item: ChatChannel, position: Int) {
            val groupSettingFragment = GroupSettingFragment(viewModel, item as Group)
            groupSettingFragment.show(
                this@ChatChannelFragment.fragmentManager!!, "name setting fragment"
            )
        }
    }

    private val personalClickCallback = object : ItemClickCallback<ChatChannel> {
        override fun onItemClick(item: ChatChannel, position: Int) {
            val arg = ChatActivityArgs(null, item as PersonalInfo)

            val intent = Intent(activity, com.gdn.android.onestop.chat.ChatActivity::class.java)
            intent.putExtras(arg.toBundle())
            startActivity(intent)
        }
    }

    private val personalOptionClick: ItemClickCallback<ChatChannel> = object : ItemClickCallback<ChatChannel> {
        override fun onItemClick(item: ChatChannel, position: Int) {

        }
    }

    private var guildRvAdapter : ChatChannelRecyclerAdapter =
        ChatChannelRecyclerAdapter(groupOptionClick, groupClickCallback)

    private var squadRvAdapter : ChatChannelRecyclerAdapter =
        ChatChannelRecyclerAdapter(groupOptionClick, groupClickCallback)

    private var tribeRvAdapter : ChatChannelRecyclerAdapter =
        ChatChannelRecyclerAdapter(groupOptionClick, groupClickCallback)

    private var personalRvAdapter : ChatChannelRecyclerAdapter =
        ChatChannelRecyclerAdapter(personalOptionClick, personalClickCallback)

    private lateinit var guildLiveData : LiveData<List<Group>>
    private lateinit var squadLiveData : LiveData<List<Group>>
    private lateinit var tribeLiveData : LiveData<List<Group>>
    private lateinit var personalLiveData : LiveData<List<PersonalInfo>>


    lateinit var icDown : Drawable
    lateinit var icRight : Drawable

    private val guildObserver = Observer<List<Group>> { groupList ->
        guildRvAdapter.updateList(groupList)
    }

    private val squadObserver = Observer<List<Group>> { groupList ->
        squadRvAdapter.updateList(groupList)
    }

    private val tribeObserver = Observer<List<Group>> { groupList ->
        tribeRvAdapter.updateList(groupList)
    }

    private val personalObserver = Observer<List<PersonalInfo>> { personalList ->
        personalRvAdapter.updateList(personalList)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(ChatListViewModel::class.java)
        viewModel.launch {
            viewModel.refreshData(false)
        }

        guildLiveData = viewModel.guildLiveData()
        squadLiveData = viewModel.squadLiveData()
        tribeLiveData = viewModel.tribeLiveData()
        personalLiveData = viewModel.personalLiveData()

        guildLiveData.observe(this, guildObserver)
        squadLiveData.observe(this, squadObserver)
        tribeLiveData.observe(this, tribeObserver)
        personalLiveData.observe(this, personalObserver)

        icDown = ResourcesCompat.getDrawable(resources, R.drawable.ic_down, null)!!
        icRight = ResourcesCompat.getDrawable(resources, R.drawable.ic_right, null)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.launch { viewModel.refreshData(false) }
        setHasOptionsMenu(true)
        databinding = FragmentChatListBinding.inflate(inflater, container, false)
        databinding.rvGuild.adapter = guildRvAdapter
        databinding.rvSquad.adapter = squadRvAdapter
        databinding.rvTribe.adapter = tribeRvAdapter
        databinding.rvPersonal.adapter = personalRvAdapter

        val guildOnclick = View.OnClickListener {
            val isCollapse : Boolean = databinding.rvGuild.visibility == View.GONE
            if(isCollapse){
                databinding.rvGuild.visibility = View.VISIBLE
                databinding.tvToggleGuild.background = icDown
            }
            else{
                databinding.rvGuild.visibility = View.GONE
                databinding.tvToggleGuild.background = icRight
            }
        }

        val squadOnclick = View.OnClickListener {
            val isCollapse : Boolean = databinding.rvSquad.visibility == View.GONE
            if(isCollapse){
                databinding.rvSquad.visibility = View.VISIBLE
                databinding.tvToggleSquad.background = icDown
            }
            else{
                databinding.rvSquad.visibility = View.GONE
                databinding.tvToggleSquad.background = icRight
            }
        }

        val tribeOnclick = View.OnClickListener {
            val isCollapse : Boolean = databinding.rvTribe.visibility == View.GONE
            if(isCollapse){
                databinding.rvTribe.visibility = View.VISIBLE
                databinding.tvToggleTribe.background = icDown
            }
            else{
                databinding.rvTribe.visibility = View.GONE
                databinding.tvToggleTribe.background = icRight
            }
        }

        val personalOnClick = View.OnClickListener {
            val isCollapse : Boolean = databinding.rvTribe.visibility == View.GONE
            if(isCollapse){
                databinding.rvPersonal.visibility = View.VISIBLE
                databinding.tvTogglePersonal.background = icDown
            }
            else{
                databinding.rvPersonal.visibility = View.GONE
                databinding.tvTogglePersonal.background = icRight
            }
        }

        databinding.tvToggleGuild.setOnClickListener(guildOnclick)
        databinding.tvToggleSquad.setOnClickListener(squadOnclick)
        databinding.tvToggleTribe.setOnClickListener(tribeOnclick)
        databinding.tvTogglePersonal.setOnClickListener(personalOnClick)

        databinding.tvGuild.setOnClickListener(guildOnclick)
        databinding.tvSquad.setOnClickListener(squadOnclick)
        databinding.tvTribe.setOnClickListener(tribeOnclick)
        databinding.tvPersonal.setOnClickListener(personalOnClick)

        databinding.swipeLayout.setOnRefreshListener {
            databinding.swipeLayout.isRefreshing = true
            viewModel.launch {
                viewModel.refreshData(true)
                databinding.swipeLayout.isRefreshing = false
            }
        }

        return databinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        guildLiveData.removeObservers(this)
        squadLiveData.removeObservers(this)
        tribeLiveData.removeObservers(this)
        personalLiveData.removeObservers(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_group, menu)
        menu.findItem(R.id.item_group_add).setOnMenuItemClickListener {
            val bottomOptionFragment =
                GroupOptionFragment(viewModel)
            bottomOptionFragment.show(
                this.fragmentManager!!, "bottom option fragment"
            )
            true
        }
    }
}