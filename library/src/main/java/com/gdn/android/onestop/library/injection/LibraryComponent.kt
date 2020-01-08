package com.gdn.android.onestop.library.injection

import com.gdn.android.onestop.base.AppComponent
import com.gdn.android.onestop.library.fragment.*
import com.gdn.android.onestop.library.util.AudioDownloadService
import com.gdn.android.onestop.library.util.BookDownloadService
import dagger.Component
import dagger.android.AndroidInjectionModule

@LibraryScope
@Component(
    modules = [AndroidInjectionModule::class, LibraryBindModule::class, LibraryProvideModule::class],
    dependencies = [AppComponent::class]
)
interface LibraryComponent {

  fun inject(bookListFragment: BookCatalogFragment)
  fun inject(audioListFragment: AudioCatalogFragment)
  fun inject(bookOptionFragment: BookOptionFragment)
  fun inject(bookReaderFragment: BookReaderFragment)
  fun inject(audioPlayerFragment: AudioPlayerFragment)
  fun inject(audioOptionFragment: AudioOptionFragment)
  fun inject(bookDownloadService: BookDownloadService)
  fun inject(audioDownloadService: AudioDownloadService)

  companion object {
    private var instance: LibraryComponent? = null

    fun getInstance(): LibraryComponent {
      var localInstance = instance
      if (localInstance == null) {
        synchronized(LibraryComponent::class) {
          localInstance = instance
          if (localInstance == null) {
            instance = DaggerLibraryComponent.factory().create(AppComponent.getInstance()!!)
            localInstance = instance
          }
        }
      }
      return instance!!
    }
  }

  @Component.Factory
  interface Factory {
    fun create(appComponent: AppComponent): LibraryComponent
  }
}