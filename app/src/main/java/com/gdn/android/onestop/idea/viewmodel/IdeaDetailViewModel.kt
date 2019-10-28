package com.gdn.android.onestop.idea.viewmodel

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.gdn.android.onestop.util.DefaultContextWrapper
import com.gdn.android.onestop.base.ObservableViewModel
import com.gdn.android.onestop.idea.data.IdeaComment
import com.gdn.android.onestop.idea.data.IdeaCommentRepository
import com.gdn.android.onestop.util.NetworkUtil
import kotlinx.coroutines.launch
import javax.inject.Inject

class IdeaDetailViewModel @Inject constructor(
    private val ideaCommentRepository: IdeaCommentRepository
) : ObservableViewModel() {

    private val commentLiveData : LiveData<PagedList<IdeaComment>> by lazy { ideaCommentRepository.getCommentLiveData() }

    lateinit var contextWrapper : DefaultContextWrapper

    var commentInput : String = ""
    @Bindable get(){return field}
    set(value) {
        field = value
        notifyPropertyChanged(BR.commentInput)
    }

    var isNotLoading : Boolean = true

    private val _reportLiveData : MutableLiveData<String> = MutableLiveData()
    val reportLiveData: LiveData<String> = _reportLiveData

    companion object {
        private const val TAG : String = "ideaDetailViewmodel"
    }

    fun setIdeaPostId(postId : String){
        viewModelScope.launch {
            ideaCommentRepository.setIdeaPost(postId)
        }
    }

    fun getPagedCommentLiveData() : LiveData<PagedList<IdeaComment>> {
        return commentLiveData
    }

    private fun isNotOnline(): Boolean {
        return !NetworkUtil.isConnectedToNetwork(contextWrapper)
    }

    private fun executeIfOnline(block : IdeaDetailViewModel.() -> Unit){
        if(isNotOnline()){
            _reportLiveData.postValue("No Connection...")
            return
        }
        this.block()
    }

    fun loadMoreComment() {
        if(!isNotLoading)return
        executeIfOnline {
            viewModelScope.launch {
                isNotLoading = false
                ideaCommentRepository.loadMoreComment()
                isNotLoading = true
            }
        }
    }

    fun submitComment(block : IdeaDetailViewModel.() -> Unit) {
        executeIfOnline {
            viewModelScope.launch {
                val isSuccess = ideaCommentRepository.submitComment(commentInput).apply{
                    commentInput = ""
                }
                Log.d(TAG,"successs")
                if(isSuccess){
                    block()
                }
            }
        }

     }

}