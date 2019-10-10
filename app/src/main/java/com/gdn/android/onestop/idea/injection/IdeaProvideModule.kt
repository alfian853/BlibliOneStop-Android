package com.gdn.android.onestop.idea.injection

import android.app.Application
import androidx.room.Room
import com.gdn.android.onestop.idea.IdeaCommentRecyclerAdapter
import com.gdn.android.onestop.idea.IdeaRecyclerAdapter
import com.gdn.android.onestop.idea.VoteHelper
import com.gdn.android.onestop.idea.data.IdeaChannelRepository
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
    fun provideIdeaRecyclerAdapter(voteHelper : VoteHelper): IdeaRecyclerAdapter {
        return IdeaRecyclerAdapter(voteHelper)
    }

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
    fun provideVoteHelper(ideaChannelRepository: IdeaChannelRepository, ideaClient: IdeaClient): VoteHelper {
        return VoteHelper(ideaChannelRepository, ideaClient)
    }

}