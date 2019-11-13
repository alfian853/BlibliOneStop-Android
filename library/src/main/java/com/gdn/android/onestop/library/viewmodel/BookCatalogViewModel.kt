package com.gdn.android.onestop.library.viewmodel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gdn.android.onestop.base.ObservableViewModel
import com.gdn.android.onestop.library.data.Book
import com.gdn.android.onestop.library.data.BookRepository
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookCatalogViewModel
@Inject constructor(private val libraryRepository: BookRepository) : ObservableViewModel() {

  val bookmarkFilter : MutableLiveData<Boolean> = MutableLiveData()
  val titleFilter : MutableLiveData<String> = MutableLiveData()

  init {
    bookmarkFilter.postValue(false)
    titleFilter.postValue("")
  }

  fun getLibraryLiveData(): MediatorLiveData<List<Book>>{
    val livedata = MediatorLiveData<List<Book>>()
    livedata.postValue(emptyList())

    val originalData = libraryRepository.getLibraryLiveData()
    livedata.addSource(originalData){
      bookmarkFilter.postValue(bookmarkFilter.value)
    }

    val filteredData: MutableLiveData<List<Book>> = MutableLiveData()

    livedata.addSource(bookmarkFilter){isBookmarkOnly ->
      val bookList = originalData.value
      if(bookList != null){
        var filteredValue = if(isBookmarkOnly){
          bookList.filter { it.isBookmarked }
        } else{
          originalData.value
        }
        filteredData.postValue(filteredValue)
        titleFilter.postValue(titleFilter.value)
      }
    }

    livedata.addSource(titleFilter){title ->
      filteredData.value?.let {bookList ->
        livedata.postValue(
          bookList.filter { it.title.contains(title, true) }
        )
      }
    }

    return livedata
  }

  fun doFetchLatestData() {
    viewModelScope.launch {
      libraryRepository.doFetchLatestData()
    }
  }

  fun downloadBook(book: Book): Observable<Int> {
    return libraryRepository.downloadBook(book)
  }

}