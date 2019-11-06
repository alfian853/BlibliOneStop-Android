package com.gdn.android.onestop.library.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.library.data.Book
import com.gdn.android.onestop.library.databinding.LayoutPageBookBinding
import com.gdn.android.onestop.library.injection.LibraryComponent
import com.gdn.android.onestop.library.util.BookDownloadService
import com.gdn.android.onestop.library.util.BookRecyclerAdapter
import com.gdn.android.onestop.library.viewmodel.BookCatalogViewModel
import javax.inject.Inject

class BookCatalogFragment : BaseFragment<LayoutPageBookBinding>() {



    override fun doFragmentInjection() {
        LibraryComponent.getInstance().inject(this)
    }

    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    @Inject
    lateinit var applicationContext : Context

    lateinit var viewModel: BookCatalogViewModel

    private val bookRecyclerAdapter: BookRecyclerAdapter = BookRecyclerAdapter()

    private val itemClick : ItemClickCallback<Book> = object : ItemClickCallback<Book> {


        override fun onItemClick(item: Book, position: Int) {

            if(item.isDownloaded){
                val args = BookReaderFragmentArgs(item)
                val bookReaderFragment = BookReaderFragment()
                bookReaderFragment.arguments = args.toBundle()
                bookReaderFragment.show(
                    fragmentManager!!, "book reader"
                )
            }
            else{
                val intent = Intent(context, BookDownloadService::class.java)
                intent.putExtra("book",item)
                context!!.startService(intent)

            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(BookCatalogViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databinding = LayoutPageBookBinding.inflate(inflater, container, false)
        viewModel.doFetchLatestData()
        viewModel.getLibraryFlow().observe(this, Observer {
            bookRecyclerAdapter.bookList = it
            bookRecyclerAdapter.notifyDataSetChanged()
        })

        bookRecyclerAdapter.itemClickCallback = itemClick
        databinding.rvBook.adapter = bookRecyclerAdapter

        return databinding.root
    }

}