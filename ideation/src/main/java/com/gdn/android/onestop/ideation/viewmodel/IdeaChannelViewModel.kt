package com.gdn.android.onestop.ideation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.gdn.android.onestop.base.util.NetworkUtil
import com.gdn.android.onestop.ideation.data.IdeaChannelRepository
import com.gdn.android.onestop.ideation.data.IdeaPost
import kotlinx.coroutines.launch
import javax.inject.Inject

class IdeaChannelViewModel @Inject constructor(
    private val ideaRepository: IdeaChannelRepository,
    private val networkUtil: NetworkUtil
) : ViewModel() {

    private val ideaLiveData : LiveData<PagedList<IdeaPost>> = ideaRepository.getIdeaLiveData()

    fun getIdeaLiveData(): LiveData<PagedList<IdeaPost>> {
        viewModelScope.launch {
            Log.d("idea","test inet")
            if(networkUtil.isConnectedToNetwork()){
                Log.d("idea","lolos")
                ideaRepository.reloadIdeaChannelData()
            }
        }
        return this.ideaLiveData
    }

    fun refreshIdeaChannelData() {
        Log.d("idea","refresh idea channel")
        viewModelScope.launch {
            if(networkUtil.isConnectedToNetwork()){
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