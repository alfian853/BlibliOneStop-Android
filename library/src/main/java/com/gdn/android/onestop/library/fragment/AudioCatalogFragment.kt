package com.gdn.android.onestop.library.fragment

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.base.ViewModelProviderFactory
import com.gdn.android.onestop.base.util.ItemClickCallback
import com.gdn.android.onestop.library.data.Audio
import com.gdn.android.onestop.library.data.LibraryDao
import com.gdn.android.onestop.library.databinding.LayoutPageAudioBinding
import com.gdn.android.onestop.library.injection.LibraryComponent
import com.gdn.android.onestop.library.util.AudioRecyclerAdapter
import com.gdn.android.onestop.library.viewmodel.AudioCatalogViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioCatalogFragment : BaseFragment<LayoutPageAudioBinding>() {

  override fun doFragmentInjection() {
    LibraryComponent.getInstance().inject(this)
  }

  @Inject
  lateinit var viewModelProviderFactory: ViewModelProviderFactory

  @Inject
  lateinit var applicationContext: Context

  @Inject
  lateinit var libraryDao: LibraryDao

  private lateinit var viewModel: AudioCatalogViewModel

  private lateinit var audioRecyclerAdapter: AudioRecyclerAdapter

  private lateinit var audioLiveData: LiveData<List<Audio>>

  private var observer: Observer<List<Audio>> = Observer {
    audioRecyclerAdapter.audioList = it
    audioRecyclerAdapter.notifyDataSetChanged()
  }

  private fun openOptionDialog(audio: Audio){
    val args = AudioOptionFragmentArgs(audio)
    val optionFragment = AudioOptionFragment()
    optionFragment.arguments = args.toBundle()
    optionFragment.show(
      fragmentManager!!, "audio option dialog"
    )
  }

  private val itemClick: ItemClickCallback<Audio> = object : ItemClickCallback<Audio> {

    override fun onItemClick(item: Audio, position: Int) {

      if (item.isDownloaded) {
        val file = item.getFile(context!!)
        if(!file.exists()){
          item.isDownloaded = false
          viewModel.launch {
            libraryDao.insertAudio(item)
          }
          Toast.makeText(context, "File not found, please redownload the audio", Toast.LENGTH_SHORT).show()
          openOptionDialog(item)
          return
        }
        else{
          val playerFragment = AudioPlayerFragment(item)
          playerFragment.show(fragmentManager!!,"")
        }

      } else {
        openOptionDialog(item)
      }

    }

  }

  private val itemLongClick: ItemClickCallback<Audio> = object : ItemClickCallback<Audio> {
    override fun onItemClick(item: Audio, position: Int) {
      openOptionDialog(item)
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel = ViewModelProvider(this, viewModelProviderFactory).get(
      AudioCatalogViewModel::class.java)
    audioRecyclerAdapter = AudioRecyclerAdapter(resources)

  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?): View? {
    databinding = LayoutPageAudioBinding.inflate(inflater, container, false)
    databinding.lifecycleOwner = this
    databinding.viewmodel = viewModel
    viewModel.doFetchLatestData()
    audioLiveData = viewModel.getAudiosLiveData()

    audioLiveData.observe(this, observer)

    audioRecyclerAdapter.itemClickCallback = itemClick
    audioRecyclerAdapter.itemLongClickCallback = itemLongClick
    databinding.rvAudio.adapter = audioRecyclerAdapter


    return databinding.root
  }

  override fun onDestroy() {
    audioLiveData.removeObserver(observer)
    super.onDestroy()
  }
}