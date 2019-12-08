package com.gdn.android.onestop.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.gdn.android.onestop.base.BaseFullScreenFragment
import com.gdn.android.onestop.library.data.Book
import com.gdn.android.onestop.library.databinding.LayoutBookReaderBinding
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle

class BookReaderFragment : BaseFullScreenFragment<LayoutBookReaderBinding>() {

  val book: Book by lazy {
    val args: BookReaderFragmentArgs by navArgs()
    args.book
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    databinding = LayoutBookReaderBinding.inflate(inflater, container, false)
    val pdf = databinding.pdfView

    pdf.fromFile(book.getFile(this.context!!))
      .spacing(4)
      .scrollHandle(DefaultScrollHandle(this.context))
      .onPageChange { page, pageCount ->
          databinding.tvPage.text = "Page: ${page+1}/$pageCount"
        }.load()

    return databinding.root
  }
}