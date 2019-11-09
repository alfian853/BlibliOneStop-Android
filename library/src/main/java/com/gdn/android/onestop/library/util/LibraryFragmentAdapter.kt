package com.gdn.android.onestop.library.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gdn.android.onestop.library.fragment.AudioCatalogFragment
import com.gdn.android.onestop.library.fragment.BookCatalogFragment

class LibraryFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

  override fun getItemCount(): Int {
    return 2
  }

  override fun createFragment(position: Int): Fragment {
    return when (position) {
      0 -> BookCatalogFragment()
      else -> AudioCatalogFragment()
    }
  }


}