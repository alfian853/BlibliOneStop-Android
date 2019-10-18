package com.gdn.android.onestop.idea.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.gdn.android.onestop.idea.data.IdeaChannelRepository
import com.gdn.android.onestop.idea.data.IdeaPost
import com.gdn.android.onestop.util.NetworkUtil
import kotlinx.coroutines.launch
import javax.inject.Inject

class IdeaChannelViewModel @Inject constructor(
    private val ideaRepository: IdeaChannelRepository
) : ViewModel() {

    private val ideaLiveData : LiveData<PagedList<IdeaPost>> = ideaRepository.getIdeaLiveData()

    lateinit var context: Context

    companion object {
        private const val TAG : String = "viewmodel"
    }

    private fun hasConnectivity() : Boolean{
        return NetworkUtil.isConnectedToNetwork(context)
    }

    fun getIdeaLiveData(): LiveData<PagedList<IdeaPost>> {
        viewModelScope.launch {
            if(::context.isInitialized && hasConnectivity()){
                ideaRepository.reloadIdeaChannelData()
            }
        }
        return this.ideaLiveData
    }

    fun refreshIdeaChannelData() {
        viewModelScope.launch {
            if(::context.isInitialized && hasConnectivity()){
                ideaRepository.reloadIdeaChannelData()
            }
        }
    }

    fun loadMoreData(){
        viewModelScope.launch {
            ideaRepository.loadMoreData()
        }
    }

}