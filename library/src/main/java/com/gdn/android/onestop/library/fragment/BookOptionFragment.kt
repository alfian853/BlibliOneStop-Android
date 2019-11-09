package com.gdn.android.onestop.library.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.gdn.android.onestop.base.BaseDialogFragment
import com.gdn.android.onestop.library.R
import com.gdn.android.onestop.library.data.Book
import com.gdn.android.onestop.library.data.LibraryDao
import com.gdn.android.onestop.library.databinding.DialogBookOptionBinding
import com.gdn.android.onestop.library.injection.LibraryComponent
import com.gdn.android.onestop.library.util.BookDownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookOptionFragment : BaseDialogFragment<DialogBookOptionBinding>(){

  override fun doFragmentInjection() {
    LibraryComponent.getInstance().inject(this)
  }

  val book : Book by lazy {
    val args : BookOptionFragmentArgs by navArgs()
    args.book
  }

  @Inject
  lateinit var libraryDao: LibraryDao

  private fun closeDialog(){
    fragmentManager?.beginTransaction()?.remove(this)?.commit()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    databinding = DialogBookOptionBinding.inflate(inflater, container, false)

    databinding.llDownload.setOnClickListener {
      val file = book.getFile(context!!)
      if(book.isDownloaded && !file.exists()){return@setOnClickListener}
      val intent = Intent(context, BookDownloadService::class.java)
      intent.putExtra("book", book)
      context!!.startService(intent)
      closeDialog()
    }

    val bookmarkText = resources.getText(R.string.bookmark_this_book)
    val deleteBookmarkText = resources.getText(R.string.delete_bookmark)

    databinding.tvBookmark.text = if(book.isBookmarked){
      deleteBookmarkText
    } else{
      bookmarkText
    }

    databinding.llBookmark.setOnClickListener {
      book.isBookmarked = !book.isBookmarked
      CoroutineScope(Dispatchers.IO).launch {
        libraryDao.insertBook(book)
      }
      closeDialog()
    }

    return databinding.root
  }
}