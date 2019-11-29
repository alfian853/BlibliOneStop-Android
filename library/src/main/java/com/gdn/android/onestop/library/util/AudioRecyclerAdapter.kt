package com.gdn.android.onestop.library.util

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.library.R
import com.gdn.android.onestop.library.data.Audio
import com.gdn.android.onestop.library.databinding.ItemAudioBinding
import com.google.android.material.card.MaterialCardView
import java.util.*

class AudioRecyclerAdapter(private val resources: Resources) :
    RecyclerView.Adapter<AudioRecyclerAdapter.AudioViewHolder>() {

  var audioList: List<Audio> = Collections.emptyList()

  var itemClickCallback: ItemClickCallback<Audio>? = null
  var itemLongClickCallback: ItemClickCallback<Audio>? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
    return AudioViewHolder(
      ItemAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun getItemCount(): Int {
    return audioList.size
  }

  override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
    val audio = audioList[position]
    holder.tvTitle.text = "Title : ${audio.title}"
    itemClickCallback?.let {callback ->
      holder.itemView.setOnClickListener {
        callback.onItemClick(audio, position)
      }
    }
    itemLongClickCallback?.let { callback ->
      holder.itemView.setOnLongClickListener {
        callback.onItemClick(audio, position)
        true
      }
    }

    if(audio.isDownloaded){
      holder.ivAudio.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_audio_green, null)
    }
    else{
      holder.ivAudio.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_audio, null)
    }

    holder.ivBookmark.visibility = if(audio.isBookmarked)View.VISIBLE
                                  else View.GONE



  }

  inner class AudioViewHolder(binding: ItemAudioBinding) : RecyclerView.ViewHolder(binding.root) {

    val tvTitle: TextView = binding.tvTitle
    val ivBookmark: ImageView = binding.ivBookmark
    val ivControl: ImageView = binding.ivControl
    val ivAudio: ImageView = binding.ivAudio
  }
}