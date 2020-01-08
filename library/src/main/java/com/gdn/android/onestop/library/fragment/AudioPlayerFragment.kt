package com.gdn.android.onestop.library.fragment

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import com.gdn.android.onestop.base.BaseDialogFragment
import com.gdn.android.onestop.library.LibraryConstant.HOUR_IN_MS
import com.gdn.android.onestop.library.LibraryConstant.MAX_PROGRESS
import com.gdn.android.onestop.library.LibraryConstant.MINUTE_IN_MS
import com.gdn.android.onestop.library.LibraryConstant.SECOND_IN_MS
import com.gdn.android.onestop.library.R
import com.gdn.android.onestop.library.data.Audio
import com.gdn.android.onestop.library.data.LibraryClient
import com.gdn.android.onestop.library.databinding.FragmentAudioPlayerBinding
import com.gdn.android.onestop.library.injection.LibraryComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.round

class AudioPlayerFragment(val audio: Audio) : BaseDialogFragment<FragmentAudioPlayerBinding>(){

  override fun doFragmentInjection() {
    LibraryComponent.getInstance().inject(this)
  }

  @Inject
  lateinit var libraryClient: LibraryClient

  lateinit var mediaPlayer: MediaPlayer

  private val twoDigitFormater = DecimalFormat("00")

  private fun Int.twoDIgit(): String = twoDigitFormater.format(this)

  fun Int.toMinuteString(): String {
    val ms = this

    val minutes = ms/MINUTE_IN_MS
    val seconds = (ms% MINUTE_IN_MS) / SECOND_IN_MS
    val hours = ms / HOUR_IN_MS
    if(hours > 0){
      return "${hours.twoDIgit()}:${minutes.twoDIgit()}:${seconds.twoDIgit()}"
    }
    return "${minutes.twoDIgit()}:${seconds.twoDIgit()}"
  }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    databinding = FragmentAudioPlayerBinding.inflate(inflater, container, false)


    mediaPlayer = MediaPlayer().apply {
      setAudioStreamType(AudioManager.STREAM_MUSIC)
      setDataSource(context!!, audio.getFile(context!!).toUri())
      prepare()
      start()
    }

    databinding.ivControl.setOnClickListener {
      if(mediaPlayer.isPlaying){
        databinding.ivControl.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_play, null)
        mediaPlayer.pause()
      }
      else{
        databinding.ivControl.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_pause, null)
        mediaPlayer.start()
      }
    }
    val handler = Handler()

    databinding.tvDuration.text = mediaPlayer.duration.toMinuteString()

    activity!!.runOnUiThread(object: Runnable{
      override fun run() {
        if(mediaPlayer.isPlaying){
          val progress = ((mediaPlayer.currentPosition.toDouble()/mediaPlayer.duration.toDouble()) * MAX_PROGRESS).toInt()
          databinding.sbProgress.progress = progress
          databinding.tvDuration.text = (mediaPlayer.duration - mediaPlayer.currentPosition).toMinuteString()

          if(progress == MAX_PROGRESS-1){
            CoroutineScope(Dispatchers.IO).launch {
              libraryClient.postAudioFinished(audio.id)
            }
          }
        }
        handler.postDelayed(this, SECOND_IN_MS.toLong())
      }
    })

    databinding.sbProgress.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
      var onScroll: Boolean = false
      override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if(onScroll){
          mediaPlayer.seekTo(((progress.toDouble()/ MAX_PROGRESS)*mediaPlayer.duration).toInt())
          mediaPlayer.start()
        }
      }

      override fun onStartTrackingTouch(seekBar: SeekBar?) {
        onScroll = true
      }

      override fun onStopTrackingTouch(seekBar: SeekBar?) {
        onScroll = false
      }
    })


    return databinding.root
  }

  override fun onDestroy() {
    super.onDestroy()
    mediaPlayer.stop()
  }
}