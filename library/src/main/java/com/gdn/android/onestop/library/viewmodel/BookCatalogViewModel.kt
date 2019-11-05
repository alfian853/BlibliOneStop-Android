package com.gdn.android.onestop.library.viewmodel

import androidx.lifecycle.viewModelScope
import com.gdn.android.onestop.base.ObservableViewModel
import com.gdn.android.onestop.library.data.Book
import com.gdn.android.onestop.library.data.BookRepository
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.launch
import javax.inject.Inject


class BookCatalogViewModel @Inject constructor(
    private val libraryRepository: BookRepository
) : ObservableViewModel(){

    fun getLibraryFlow() = libraryRepository.getLibraryLiveData()
    fun doFetchLatestData(){
        viewModelScope.launch {
            libraryRepository.doFetchLatestData()
        }
    }

    fun downloadBook(book : Book): Observable<Int> {
        return libraryRepository.downloadBook(book)
    }
}