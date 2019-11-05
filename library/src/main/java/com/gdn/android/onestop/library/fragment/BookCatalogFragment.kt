package com.gdn.android.onestop.library.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.library.R
import com.gdn.android.onestop.library.data.Book
import com.gdn.android.onestop.library.databinding.LayoutPageBookBinding
import com.gdn.android.onestop.library.injection.LibraryComponent
import com.gdn.android.onestop.library.util.BookRecyclerAdapter
import com.gdn.android.onestop.library.util.Constant
import com.gdn.android.onestop.library.viewmodel.BookCatalogViewModel
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class BookCatalogFragment : BaseFragment<LayoutPageBookBinding>() {

    private val downloadNotifId = 5

    override fun doFragmentInjection() {
        LibraryComponent.getInstance().inject(this)
    }

    @Inject
    lateinit var viewModelProviderFactory : ViewModelProviderFactory

    lateinit var viewModel: BookCatalogViewModel

    private val bookRecyclerAdapter: BookRecyclerAdapter = BookRecyclerAdapter()

    val itemClick : ItemClickCallback<Book> = object : ItemClickCallback<Book> {


        override fun onItemClick(item: Book, position: Int) {

            if(item.isDownloaded){

            }
            else{
                downloadBook(item)
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

    private fun downloadBook(book: Book){
        val notification = NotificationCompat.Builder(this@BookCatalogFragment.context!!, Constant.NOTIF_DOWNLOAD_ID)
            .setSmallIcon(R.drawable.ic_default_user)
            .setContentTitle("Downloading book ${book.title}")
            .setContentText("Download in progress")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, 0, false)

        val notificationManager = NotificationManagerCompat.from(this@BookCatalogFragment.context!!)
        notificationManager.notify(downloadNotifId, notification.build())

        viewModel.downloadBook(book)
            .subscribeOn(Schedulers.io())
            .subscribe({
                if(it == 100){
                    notification.setContentText("Download finished")
                        .setProgress(0, 0, false)
                        .setOngoing(false)
                    notificationManager.notify(downloadNotifId, notification.build())
                }
                else{
                    notification.setProgress(100, it, false)
                    notificationManager.notify(downloadNotifId, notification.build())
                    Log.d("book","update percentage $it")
                }
            },{
                notification.setContentText("Download failed")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                notificationManager.notify(downloadNotifId, notification.build())
            })

    }
}