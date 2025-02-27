package com.gdn.android.onestop.ideation.viewmodel

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

    fun getCommentsLiveData() : LiveData<List<IdeaComment>> = ideaCommentRepository.getCommentsLiveData()

    private fun executeIfOnline(block : Any.() -> Unit){
        if(!networkUtil.isConnectedToNetwork()){
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

    fun submitComment(onSuccess : Any.() -> Unit) {
        executeIfOnline {
            launch {
                val tmp = commentInput
                commentInput = ""
                val isSuccess = ideaCommentRepository.submitComment(tmp)
                if(isSuccess){
                    onSuccess()
                }
            }
        }

     }

}