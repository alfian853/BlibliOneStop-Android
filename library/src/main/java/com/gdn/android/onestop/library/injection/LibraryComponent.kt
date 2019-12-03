package com.gdn.android.onestop.library.injection

import com.gdn.android.onestop.base.BaseComponent
import com.gdn.android.onestop.library.fragment.AudioCatalogFragment
import com.gdn.android.onestop.library.fragment.AudioOptionFragment
import com.gdn.android.onestop.library.fragment.BookCatalogFragment
import com.gdn.android.onestop.library.fragment.BookOptionFragment
import com.gdn.android.onestop.library.util.AudioDownloadService
import com.gdn.android.onestop.library.util.BookDownloadService
import dagger.Component
import dagger.android.AndroidInjectionModule

@LibraryScope
@Component(
    modules = [AndroidInjectionModule::class, LibraryBindModule::class, LibraryProvideModule::class],
    dependencies = [BaseComponent::class]
)
interface LibraryComponent {

  fun inject(bookListFragment: BookCatalogFragment)
  fun inject(audioListFragment: AudioCatalogFragment)
  fun inject(bookOptionFragment: BookOptionFragment)
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
            instance = DaggerLibraryComponent.factory().create(BaseComponent.getInstance()!!)
            localInstance = instance
          }
        }
      }
      return instance!!
    }
  }

  @Component.Factory
  interface Factory {
    fun create(baseComponent: BaseComponent): LibraryComponent
  }
}