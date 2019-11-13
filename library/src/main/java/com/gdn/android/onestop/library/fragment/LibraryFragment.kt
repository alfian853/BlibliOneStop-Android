package com.gdn.android.onestop.library.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdn.android.onestop.base.BaseFragment
import com.gdn.android.onestop.library.R
import com.gdn.android.onestop.library.databinding.LayoutLibraryBinding
import com.gdn.android.onestop.library.util.Constant
import com.gdn.android.onestop.library.util.LibraryFragmentAdapter
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : BaseFragment<LayoutLibraryBinding>() {

  private val downloadNotifChannel = "onestop-book-download"

  override fun doFragmentInjection() {
  }

  lateinit var tabLayoutMediator: TabLayoutMediator

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    databinding = LayoutLibraryBinding.inflate(inflater, container, false)

    databinding.vpLibrary.adapter = LibraryFragmentAdapter(this)

    tabLayoutMediator = TabLayoutMediator(databinding.tlLibrary, databinding.vpLibrary,
      TabLayoutMediator.TabConfigurationStrategy { tab, position -> Log.d("library-onestop", "mediator $position")
        when (position) {
          0 -> tab.text = resources.getText(R.string.book)
          1 -> tab.text = resources.getText(R.string.audio)
        }
      })

    tabLayoutMediator.attach()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val downloadNotifChannel = NotificationChannel(Constant.NOTIF_DOWNLOAD_ID, "download",
          NotificationManager.IMPORTANCE_HIGH)
      val notificationManager = this.activity!!.getSystemService(NotificationManager::class.java)!!
      notificationManager.createNotificationChannel(downloadNotifChannel)
    }

    return databinding.root
  }

  override fun onDestroyView() {
    Log.d("bookmark","destroy main")
    tabLayoutMediator.detach()

    super.onDestroyView()
  }
}