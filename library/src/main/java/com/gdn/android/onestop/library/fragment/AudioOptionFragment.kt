package com.gdn.android.onestop.library.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.gdn.android.onestop.base.BaseDialogFragment
import com.gdn.android.onestop.library.LibraryConstant
import com.gdn.android.onestop.library.R
import com.gdn.android.onestop.library.data.Audio
import com.gdn.android.onestop.library.data.LibraryDao
import com.gdn.android.onestop.library.databinding.DialogAudioOptionBinding
import com.gdn.android.onestop.library.injection.LibraryComponent
import com.gdn.android.onestop.library.util.AudioDownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioOptionFragment : BaseDialogFragment<DialogAudioOptionBinding>(){

  override fun doFragmentInjection() {
    LibraryComponent.getInstance().inject(this)
  }

  val audio : Audio by lazy {
    val args : AudioOptionFragmentArgs by navArgs()
    args.audio
  }

  @Inject
  lateinit var libraryDao: LibraryDao

  private fun closeDialog(){
    fragmentManager?.beginTransaction()?.remove(this)?.commit()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    databinding = DialogAudioOptionBinding.inflate(inflater, container, false)
    Log.d("haihai","hey")
    databinding.tvTitle.text = audio.title

    databinding.llDownload.setOnClickListener {
      val file = audio.getFile(context!!)
      if(audio.isDownloaded && !file.exists()){return@setOnClickListener}
      val intent = Intent(context, AudioDownloadService::class.java)
      intent.putExtra(LibraryConstant.AUDIO, audio)
      context!!.startService(intent)
      closeDialog()
    }

    val bookmarkText = resources.getText(R.string.bookmark_this_audio)
    val deleteBookmarkText = resources.getText(R.string.delete_bookmark)

    databinding.tvBookmark.text = if(audio.isBookmarked){
      deleteBookmarkText
    } else{
      bookmarkText
    }

    databinding.llBookmark.setOnClickListener {
      audio.isBookmarked = !audio.isBookmarked
      CoroutineScope(Dispatchers.IO).launch {
        libraryDao.insertAudio(audio)
      }
      closeDialog()
    }

    return databinding.root
  }
}