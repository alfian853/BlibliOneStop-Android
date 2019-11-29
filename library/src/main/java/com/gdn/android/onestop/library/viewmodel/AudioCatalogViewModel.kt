package com.gdn.android.onestop.library.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.library.data.Audio
import com.gdn.android.onestop.library.data.AudioRepository
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioCatalogViewModel
@Inject constructor(private val audioRepository: AudioRepository) : BaseViewModel() {

  val bookmarkFilter : MutableLiveData<Boolean> = MutableLiveData()
  val titleFilter : MutableLiveData<String> = MutableLiveData()

  init {
    bookmarkFilter.postValue(false)
    titleFilter.postValue("")
  }

  fun getAudiosLiveData(): LiveData<List<Audio>> {
    val livedata = MediatorLiveData<List<Audio>>()
    livedata.postValue(emptyList())

    val originalData = audioRepository.getLibraryLiveData()
    livedata.addSource(originalData){
      bookmarkFilter.postValue(bookmarkFilter.value)
    }

    val filteredData: MutableLiveData<List<Audio>> = MutableLiveData()

    livedata.addSource(bookmarkFilter){isAudiomarkOnly ->
      val AudioList = originalData.value
      if(AudioList != null){
        var filteredValue = if(isAudiomarkOnly){
          AudioList.filter { it.isBookmarked }
        } else{
          originalData.value
        }
        filteredData.postValue(filteredValue)
        titleFilter.postValue(titleFilter.value)
      }
    }

    livedata.addSource(titleFilter){title ->
      filteredData.value?.let {AudioList ->
        livedata.postValue(
          AudioList.filter { it.title.contains(title, true) }
        )
      }
    }

    return livedata
  }

  fun doFetchLatestData() {
    launch {
      audioRepository.doFetchLatestData()
    }
  }

  fun downloadAudio(Audio: Audio): Observable<Int> {
    return audioRepository.downloadAudio(Audio)
  }

}