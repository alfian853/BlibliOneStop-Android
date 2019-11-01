package com.gdn.android.onestop.ideation.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.gdn.android.onestop.base.ObservableViewModel
import com.gdn.android.onestop.ideation.data.IdeaChannelRepository
import javax.inject.Inject


class IdeaCreateViewModel
@Inject constructor(private val ideaChannelRepository: IdeaChannelRepository) : ObservableViewModel() {

    var ideaContent : String = ""
    @Bindable
    get(){return field}
    set(value){
        field = value
        notifyPropertyChanged(BR.ideaContent)
    }

    suspend fun postIdea(){
        ideaChannelRepository.postIdea(ideaContent)
    }

}