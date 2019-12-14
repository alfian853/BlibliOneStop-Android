package com.gdn.android.onestop.ideation.injection

import android.app.Application
import androidx.room.Room
import com.gdn.android.onestop.ideation.util.IdeaCommentRecyclerAdapter
import com.gdn.android.onestop.ideation.util.IdeaRecyclerAdapter
import com.gdn.android.onestop.ideation.util.VoteHelper
import com.gdn.android.onestop.ideation.data.IdeaChannelRepository
import com.gdn.android.onestop.ideation.data.IdeaClient
import com.gdn.android.onestop.ideation.data.IdeaDao
import com.gdn.android.onestop.ideation.data.IdeaDatabase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class IdeaProvideModule {

    @IdeaScope
    @Provides
    fun provideIdeaCommentRecyclerAdapter(): IdeaCommentRecyclerAdapter {
        return IdeaCommentRecyclerAdapter()
    }

    @IdeaScope
    @Provides
    fun provideIdeaClient(retrofit: Retrofit) : IdeaClient {
        return retrofit.create(IdeaClient::class.java)
    }

    @IdeaScope
    @Provides
    fun provideIdeaDatabase(application: Application) : IdeaDatabase {
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

    @IdeaScope
    @Provides
    fun provideIdeaChannelRepository(ideaDao: IdeaDao, ideaClient: IdeaClient): IdeaChannelRepository {
        return IdeaChannelRepository(ideaDao, ideaClient)
    }

    @IdeaScope
    @Provides
    fun provideVoteHelper(ideaChannelRepository: IdeaChannelRepository, ideaClient: IdeaClient): VoteHelper {
        return VoteHelper(ideaChannelRepository, ideaClient)
    }

}