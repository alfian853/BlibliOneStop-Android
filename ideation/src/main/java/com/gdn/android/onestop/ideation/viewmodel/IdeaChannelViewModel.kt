package com.gdn.android.onestop.ideation.viewmodel

import androidx.lifecycle.LiveData
import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.base.util.NetworkUtil
import com.gdn.android.onestop.ideation.data.IdeaChannelRepository
import com.gdn.android.onestop.ideation.data.IdeaPost
import kotlinx.coroutines.launch
import javax.inject.Inject

class IdeaChannelViewModel @Inject constructor(
    private val ideaRepository: IdeaChannelRepository,
    private val networkUtil: NetworkUtil
) : BaseViewModel() {

    private val ideaLiveData : LiveData<List<IdeaPost>> = ideaRepository.getIdeaLiveData()

    fun getIdeaLiveData(): LiveData<List<IdeaPost>> {
        launch {
            if(networkUtil.isConnectedToNetwork()){
                ideaRepository.reloadIdeaChannelData()
            }
        }
        return this.ideaLiveData
    }

    fun refreshIdeaChannelData() {
        launch {
            if(networkUtil.isConnectedToNetwork()){
                ideaRepository.reloadIdeaChannelData()
            }
        }
    }

    fun loadMoreData(){
        launch {
            ideaRepository.loadMoreData()
        }
    }

}