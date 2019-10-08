package com.gdn.android.onestop.idea.injection

import android.app.Application
import androidx.room.Room
import com.gdn.android.onestop.idea.IdeaRecyclerViewAdapter
import com.gdn.android.onestop.idea.data.IdeaClient
import com.gdn.android.onestop.idea.data.IdeaDao
import com.gdn.android.onestop.idea.data.IdeaDatabase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class IdeaProvideModule {

    @IdeaScope
    @Provides
    fun provideIdeaRecyclerAdapter(): IdeaRecyclerViewAdapter {
        return IdeaRecyclerViewAdapter()
    }

    @IdeaScope
    @Provides
    fun bindIdeaClient(retrofit: Retrofit) : IdeaClient {
        return retrofit.create(IdeaClient::class.java)
    }

    @IdeaScope
    @Provides
    fun bindIdeaDatabase(application: Application) : IdeaDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            IdeaDatabase::class.java, "idea_database"
        ).fallbackToDestructiveMigration().build()
    }

    @IdeaScope
    @Provides
    fun provideIdeaDao(ideaDatabase: IdeaDatabase): IdeaDao {
        return ideaDatabase.IdeaDao()
    }

}