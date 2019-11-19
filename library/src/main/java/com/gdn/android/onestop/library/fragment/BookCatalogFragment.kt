package com.gdn.android.onestop.library.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.base.util.ItemSpacingDecoration
import com.gdn.android.onestop.library.data.Book
import com.gdn.android.onestop.library.data.LibraryDao
import com.gdn.android.onestop.library.databinding.LayoutPageBookBinding
import com.gdn.android.onestop.library.injection.LibraryComponent
import com.gdn.android.onestop.library.util.BookRecyclerAdapter
import com.gdn.android.onestop.library.viewmodel.BookCatalogViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookCatalogFragment : BaseFragment<LayoutPageBookBinding>() {

  override fun doFragmentInjection() {
    LibraryComponent.getInstance().inject(this)
  }

  @Inject
  lateinit var viewModelProviderFactory: ViewModelProviderFactory

  @Inject
  lateinit var applicationContext: Context

  @Inject
  lateinit var libraryDao: LibraryDao

  private lateinit var viewModel: BookCatalogViewModel

  private lateinit var bookRecyclerAdapter: BookRecyclerAdapter

  private lateinit var bookLiveData: LiveData<List<Book>>

  private var observer: Observer<List<Book>> = Observer {
    bookRecyclerAdapter.bookList = it
    bookRecyclerAdapter.notifyDataSetChanged()
  }

  private fun openOptionDialog(book : Book){
    val args = BookOptionFragmentArgs(book)
    val optionFragment = BookOptionFragment()
    optionFragment.arguments = args.toBundle()
    optionFragment.show(
      fragmentManager!!, "book option dialog"
    )
  }

  private val itemClick: ItemClickCallback<Book> = object : ItemClickCallback<Book> {

    override fun onItemClick(item: Book, position: Int) {

      if (item.isDownloaded) {
        val file = item.getFile(context!!)
        if(!file.exists()){
          item.isDownloaded = false
          viewModel.launch {
            libraryDao.insertBook(item)
          }
          Toast.makeText(context, "File not found, please redownload the book", Toast.LENGTH_SHORT).show()
          openOptionDialog(item)
          return
        }
        val args = BookReaderFragmentArgs(item)
        val bookReaderFragment = BookReaderFragment()
        bookReaderFragment.arguments = args.toBundle()
        bookReaderFragment.show(fragmentManager!!, "book reader")
      } else {
        openOptionDialog(item)
      }

    }

  }

  private val itemLongClick: ItemClickCallback<Book> = object : ItemClickCallback<Book> {
    override fun onItemClick(item: Book, position: Int) {
      openOptionDialog(item)
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel = ViewModelProvider(this, viewModelProviderFactory).get(
      BookCatalogViewModel::class.java)
    bookRecyclerAdapter = BookRecyclerAdapter(resources)

  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?): View? {
    databinding = LayoutPageBookBinding.inflate(inflater, container, false)
    databinding.lifecycleOwner = this
    databinding.viewmodel = viewModel
    viewModel.doFetchLatestData()
    bookLiveData = viewModel.getLibraryLiveData()

    bookLiveData.observe(this, observer)

    bookRecyclerAdapter.itemClickCallback = itemClick
    bookRecyclerAdapter.itemLongClickCallback = itemLongClick
    databinding.rvBook.adapter = bookRecyclerAdapter
    databinding.rvBook.addItemDecoration(ItemSpacingDecoration(60))



    return databinding.root
  }

  override fun onDestroy() {
    bookLiveData.removeObserver(observer)
    super.onDestroy()
  }
}