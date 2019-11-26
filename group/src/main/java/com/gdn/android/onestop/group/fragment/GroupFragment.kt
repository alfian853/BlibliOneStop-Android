package com.gdn.android.onestop.group.fragment

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
import com.gdn.android.onestop.group.GroupActivity
import com.gdn.android.onestop.group.GroupActivityArgs
import com.gdn.android.onestop.group.R
import com.gdn.android.onestop.group.data.Group
import com.gdn.android.onestop.group.databinding.FragmentGroupBinding
import com.gdn.android.onestop.group.injection.GroupComponent
import com.gdn.android.onestop.group.util.GroupRecyclerAdapter
import com.gdn.android.onestop.group.viewmodel.GroupViewModel
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

    private val groupClickCallback = object :
        ItemClickCallback<Group> {
        override fun onItemClick(item: Group, position: Int) {
            val arg = GroupActivityArgs(item)

            val intent = Intent(activity, GroupActivity::class.java)
            intent.putExtras(arg.toBundle())
            startActivity(intent)
        }
    }

    val groupOptionClick = object : ItemClickCallback<Group> {
        override fun onItemClick(item: Group, position: Int) {
            val groupSettingFragment =
                GroupSettingFragment(viewModel, item)
            groupSettingFragment.show(
                this@GroupFragment.fragmentManager!!, "group setting fragment"
            )
        }

    }

    var guildRvAdapter : GroupRecyclerAdapter =
        GroupRecyclerAdapter()
            .apply {
                nameClickCallback = this@GroupFragment.groupClickCallback
                optionClickCallback = this@GroupFragment.groupOptionClick
            }

    var squadRvAdapter : GroupRecyclerAdapter =
        GroupRecyclerAdapter().apply {
            nameClickCallback = groupClickCallback
        }
    var tribeRvAdapter : GroupRecyclerAdapter =
        GroupRecyclerAdapter().apply {
            nameClickCallback = groupClickCallback
        }

    private val guildLiveData : LiveData<List<Group>> by lazy { viewModel.guildLiveData() }
    private val squadLiveData : LiveData<List<Group>> by lazy { viewModel.squadLiveData() }
    private val tribeLiveData : LiveData<List<Group>> by lazy { viewModel.tribeLiveData() }


    lateinit var icDown : Drawable
    lateinit var icRight : Drawable


    private val observer = Observer<List<Group>> {
        if(it.isNotEmpty()){
            when(it[0].type){
                Group.Type.GUILD -> guildRvAdapter
                Group.Type.SQUAD -> squadRvAdapter
                Group.Type.TRIBE -> tribeRvAdapter
            }.updateList(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(GroupViewModel::class.java)
        viewModel.refreshData(false)
        guildLiveData.observe(this, observer)
        squadLiveData.observe(this, observer)
        tribeLiveData.observe(this, observer)
        icDown = ResourcesCompat.getDrawable(resources, R.drawable.ic_down, null)!!
        icRight = ResourcesCompat.getDrawable(resources, R.drawable.ic_right, null)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        guildLiveData.removeObserver(observer)
        squadLiveData.removeObserver(observer)
        tribeLiveData.removeObserver(observer)
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