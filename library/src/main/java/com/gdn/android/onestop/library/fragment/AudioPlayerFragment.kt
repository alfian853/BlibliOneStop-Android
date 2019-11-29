package com.gdn.android.onestop.library.fragment

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.gdn.android.onestop.library.R
import com.gdn.android.onestop.library.data.Audio
import com.gdn.android.onestop.library.databinding.FragmentAudioPlayerBinding
import java.text.DecimalFormat

class AudioPlayerFragment(val audio: Audio) : DialogFragment(){

  lateinit var binding: FragmentAudioPlayerBinding

  lateinit var mediaPlayer: MediaPlayer

  val twoDigitFormater = DecimalFormat("00")

  private fun Int.twoDIgit(): String = twoDigitFormater.format(this)

  fun Int.toMinuteString(): String {
    val ms = this

    val minutes = ms/60000
    val seconds = (ms%60000)/1000
    val hours = ms / 3600000
    if(hours > 0){
      return "${hours.twoDIgit()}:${minutes.twoDIgit()}:${seconds.twoDIgit()}"
    }
    return "${minutes.twoDIgit()}:${seconds.twoDIgit()}"
  }


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)


    mediaPlayer = MediaPlayer().apply {
      setAudioStreamType(AudioManager.STREAM_MUSIC)
      setDataSource(context!!, audio.getFile(context!!).toUri())
      prepare()
      start()
    }

    binding.ivControl.setOnClickListener {
      if(mediaPlayer.isPlaying){
        binding.ivControl.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_play, null)
        mediaPlayer.pause()
      }
      else{
        binding.ivControl.background = ResourcesCompat.getDrawable(resources, R.drawable.ic_pause, null)
        mediaPlayer.start()
      }
    }
    val handler = Handler()

    binding.tvDuration.text = mediaPlayer.duration.toMinuteString()

    activity!!.runOnUiThread(object: Runnable{
      override fun run() {
        if(mediaPlayer.isPlaying){
          binding.sbProgress.progress = ((mediaPlayer.currentPosition.toDouble()/mediaPlayer.duration.toDouble())*100).toInt()
          binding.tvDuration.text = (mediaPlayer.duration - mediaPlayer.currentPosition).toMinuteString()
        }
        handler.postDelayed(this, 1000)
      }
    })

    binding.sbProgress.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
      override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        mediaPlayer.seekTo(((progress.toDouble()/100)*mediaPlayer.duration).toInt())
        mediaPlayer.start()
      }

      override fun onStartTrackingTouch(seekBar: SeekBar?) {
      }

      override fun onStopTrackingTouch(seekBar: SeekBar?) {
      }
    })


    return binding.root
  }

  override fun onDestroy() {
    super.onDestroy()
    mediaPlayer.stop()
  }
}