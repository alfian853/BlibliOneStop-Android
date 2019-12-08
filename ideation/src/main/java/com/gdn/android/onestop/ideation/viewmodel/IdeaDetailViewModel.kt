package com.gdn.android.onestop.ideation.viewmodel

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gdn.android.onestop.base.util.DefaultContextWrapper
import com.gdn.android.onestop.base.util.NetworkUtil
import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.ideation.data.IdeaComment
import com.gdn.android.onestop.ideation.data.IdeaCommentRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class IdeaDetailViewModel @Inject constructor(
    private val ideaCommentRepository: IdeaCommentRepository,
    private val networkUtil: NetworkUtil
) : BaseViewModel() {

    private val commentLiveData : LiveData<List<IdeaComment>> by lazy { ideaCommentRepository.getCommentsLiveData() }

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
        ideaCommentRepository.setIdeaPost(postId)
    }

    fun getCommentsLiveData() : LiveData<List<IdeaComment>> {
        return commentLiveData
    }

    private fun isNotOnline(): Boolean {
        return !networkUtil.isConnectedToNetwork()
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
            launch {
                isNotLoading = false
                ideaCommentRepository.loadMoreComment()
                isNotLoading = true
            }
        }
    }

    fun submitComment(block : IdeaDetailViewModel.() -> Unit) {
        executeIfOnline {
            launch {
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