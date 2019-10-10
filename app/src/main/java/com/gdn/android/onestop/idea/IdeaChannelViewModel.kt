package com.gdn.android.onestop.idea

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.gdn.android.onestop.idea.data.IdeaChannelRepository
import com.gdn.android.onestop.idea.data.IdeaPost
import com.gdn.android.onestop.util.NetworkUtil
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
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
        if(::context.isInitialized && hasConnectivity()){
            ideaRepository.reloadIdeaChannelData().subscribeOn(Schedulers.io()).subscribe()
        }
        return this.ideaLiveData
    }

    fun refreshIdeaChannelData() : Single<Boolean> {
        if(::context.isInitialized && hasConnectivity()){
            return ideaRepository.reloadIdeaChannelData()
        }
        return Single.create { it.onSuccess(false) }
    }

}