package com.gdn.android.onestop.library.injection

import androidx.lifecycle.ViewModel
import com.gdn.android.onestop.base.ViewModelKey
import com.gdn.android.onestop.library.viewmodel.AudioCatalogViewModel
import com.gdn.android.onestop.library.viewmodel.BookCatalogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module abstract class LibraryBindModule {

  @LibraryScope
  @Binds
  @IntoMap
  @ViewModelKey(BookCatalogViewModel::class)
  abstract fun bindBookCatalogViewModel(bookCatalogViewModel: BookCatalogViewModel): ViewModel

  @LibraryScope
  @Binds
  @IntoMap
  @ViewModelKey(AudioCatalogViewModel::class)
  abstract fun bindAudioCatalogViewModel(audioCatalogViewModel: AudioCatalogViewModel): ViewModel

}