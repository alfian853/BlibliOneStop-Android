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
import com.gdn.android.onestop.base.util.Navigator
import com.gdn.android.onestop.chat.ChatActivity
import com.gdn.android.onestop.chat.ChatActivityArgs
import com.gdn.android.onestop.chat.R
import com.gdn.android.onestop.chat.data.Group
import com.gdn.android.onestop.chat.data.GroupDao
import com.gdn.android.onestop.chat.databinding.FragmentGroupBinding
import com.gdn.android.onestop.chat.injection.GroupComponent
import com.gdn.android.onestop.chat.util.GroupRecyclerAdapter
import com.gdn.android.onestop.chat.viewmodel.GroupViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupFragment : BaseFragment<FragmentGroupBinding>() {

    override fun doFragmentInjection() {
        GroupComponent.getInstance().inject(this)
    }

    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    @Inject
    lateinit var viewModel: GroupViewModel

    @Inject
    lateinit var groupDao: GroupDao

    private val groupClickCallback = object :
        ItemClickCallback<Group> {
        override fun onItemClick(item: Group, position: Int) {
            val arg = ChatActivityArgs(item)

            val intent = Intent(activity, com.gdn.android.onestop.chat.ChatActivity::class.java)
            intent.putExtras(arg.toBundle())
            startActivity(intent)
        }
    }

    private val groupOptionClick = object : ItemClickCallback<Group> {
        override fun onItemClick(item: Group, position: Int) {
            val groupSettingFragment = GroupSettingFragment(viewModel, item)
            groupSettingFragment.show(
                this@GroupFragment.fragmentManager!!, "group setting fragment"
            )
        }

    }

    private var guildRvAdapter : GroupRecyclerAdapter =
        GroupRecyclerAdapter(groupOptionClick, groupClickCallback)

    private var squadRvAdapter : GroupRecyclerAdapter =
        GroupRecyclerAdapter(groupOptionClick, groupClickCallback)

    private var tribeRvAdapter : GroupRecyclerAdapter =
        GroupRecyclerAdapter(groupOptionClick, groupClickCallback)

    private lateinit var guildLiveData : LiveData<List<Group>>
    private lateinit var squadLiveData : LiveData<List<Group>>// by lazy { viewModel.squadLiveData() }
    private lateinit var tribeLiveData : LiveData<List<Group>>// by lazy { viewModel.tribeLiveData() }


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(GroupViewModel::class.java)
        viewModel.launch {
            viewModel.refreshData(false)
        }

        guildLiveData = viewModel.guildLiveData()
        squadLiveData = viewModel.squadLiveData()
        tribeLiveData = viewModel.tribeLiveData()

        guildLiveData.observe(this, guildObserver)
        squadLiveData.observe(this, squadObserver)
        tribeLiveData.observe(this, tribeObserver)
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
        databinding = FragmentGroupBinding.inflate(inflater, container, false)
        databinding.rvGuild.adapter = guildRvAdapter
        databinding.rvSquad.adapter = squadRvAdapter
        databinding.rvTribe.adapter = tribeRvAdapter

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

        databinding.tvToggleGuild.setOnClickListener(guildOnclick)
        databinding.tvToggleSquad.setOnClickListener(squadOnclick)
        databinding.tvToggleTribe.setOnClickListener(tribeOnclick)

        databinding.tvGuild.setOnClickListener(guildOnclick)
        databinding.tvSquad.setOnClickListener(squadOnclick)
        databinding.tvTribe.setOnClickListener(tribeOnclick)


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