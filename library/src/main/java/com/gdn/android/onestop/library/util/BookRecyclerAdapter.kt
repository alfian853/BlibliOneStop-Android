package com.gdn.android.onestop.library.util

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.library.R
import com.gdn.android.onestop.library.data.Book
import com.google.android.material.card.MaterialCardView
import java.util.*

class BookRecyclerAdapter(private val resources: Resources) :
    RecyclerView.Adapter<BookRecyclerAdapter.BookViewHolder>() {

  var bookList: List<Book> = Collections.emptyList()

  var itemClickCallback: ItemClickCallback<Book>? = null
  var itemLongClickCallback: ItemClickCallback<Book>? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
    return BookViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false))
  }

  override fun getItemCount(): Int {
    return bookList.size
  }

  override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
    val book = bookList[position]
    holder.tvTitle.text = "Title : ${book.title}"
    holder.tvRead.text = "Read : 0 / ${book.totalPages}"
    Glide.with(holder.ivBookPict.context).load(book.thumbnailUrl).into(holder.ivBookPict)
    itemClickCallback?.let {callback ->
      holder.itemView.setOnClickListener {
        callback.onItemClick(book, position)
      }
    }
    itemLongClickCallback?.let { callback ->
      holder.itemView.setOnLongClickListener {
        callback.onItemClick(book, position)
        true
      }
    }

    (holder.itemView as MaterialCardView).strokeColor = if (book.isDownloaded) {
      ResourcesCompat.getColor(resources, R.color.green,null)
    } else {
      ResourcesCompat.getColor(resources, R.color.grey,null)
    }

    holder.ivBookmark.visibility = if(book.isBookmarked)View.VISIBLE
                                  else View.GONE



  }

  inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    val tvRead: TextView = itemView.findViewById(R.id.tv_read)
    val ivBookmark: ImageView = itemView.findViewById(R.id.iv_bookmark)
    val ivBookPict: ImageView = itemView.findViewById(R.id.iv_book_pict)
  }
}