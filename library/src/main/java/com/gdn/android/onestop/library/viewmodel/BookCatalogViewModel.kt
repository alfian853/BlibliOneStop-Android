package com.gdn.android.onestop.library.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gdn.android.onestop.base.BaseViewModel
import com.gdn.android.onestop.library.data.Book
import com.gdn.android.onestop.library.data.BookRepository
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookCatalogViewModel
@Inject constructor(private val bookRepository: BookRepository) : BaseViewModel() {

  val bookmarkFilter : MutableLiveData<Boolean> = MutableLiveData()
  val titleFilter : MutableLiveData<String> = MutableLiveData()

  init {
    bookmarkFilter.postValue(false)
    titleFilter.postValue("")
  }

  fun getBooksLiveData(): MediatorLiveData<List<Book>>{
    val livedata = MediatorLiveData<List<Book>>()
    livedata.postValue(emptyList())

    val originalData = bookRepository.getBooksLiveData()
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
    launch {
      bookRepository.doFetchLatestData()
    }
  }

  fun downloadBook(book: Book): Observable<Int> {
    return bookRepository.downloadBook(book)
  }

}