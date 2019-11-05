package com.gdn.android.onestop.library.injection

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.library.data.*
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class LibraryProvideModule {

    @LibraryScope
    @Provides
    fun provideLibraryClient(retrofit: Retrofit): LibraryClient {
        return retrofit.create(LibraryClient::class.java)
    }

    @LibraryScope
    @Provides
    fun provideLibraryDatabase(application: Application) : LibraryDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            LibraryDatabase::class.java, "library_database"
        ).fallbackToDestructiveMigration().build()
    }

    @LibraryScope
    @Provides
    fun provideLibraryDao(libraryDatabase: LibraryDatabase): LibraryDao {
        return libraryDatabase.libraryDao()
    }

    @LibraryScope
    @Provides
    fun provideBookRepository(context : Context,
                              libraryClient: LibraryClient,
                              libraryDao: LibraryDao,
                              bookUpdateManager: BookUpdateManager): BookRepository {
        return BookRepository(context, libraryDao, libraryClient, bookUpdateManager)
    }

    @LibraryScope
    @Provides
    fun provideBookUpdateManager(application: Application, sessionManager: SessionManager): BookUpdateManager {
        return BookUpdateManager(application, sessionManager)
    }

}