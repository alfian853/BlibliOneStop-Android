package com.gdn.android.onestop.chat.injection

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.gdn.android.onestop.base.util.SessionManager
import com.gdn.android.onestop.chat.data.*
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class GroupProvideModule {

    @GroupScope
    @Provides
    fun provideGroupClient(retrofit: Retrofit) : GroupClient {
        return retrofit.create(GroupClient::class.java)
    }

    @GroupScope
    @Provides
    fun provideChatClient(retrofit: Retrofit) : ChatClient {
        return retrofit.create(ChatClient::class.java)
    }

    @GroupScope
    @Provides
    fun provideGroupDatabase(application: Application) : ChatDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            ChatDatabase::class.java, "group_database"
        ).fallbackToDestructiveMigration().build()
    }

    @GroupScope
    @Provides
    fun provideGroupDao(groupDatabase: ChatDatabase) : GroupDao {
        return groupDatabase.groupDao()
    }

    @GroupScope
    @Provides
    fun provideChatDao(groupDatabase: ChatDatabase) : ChatDao {
        return groupDatabase.chatDao()
    }

    @GroupScope
    @Provides
    fun provideGroupRepository(groupDao: GroupDao, groupClient: GroupClient, groupUpdateManager: GroupUpdateManager): GroupRepository {
        return GroupRepository(
            groupDao,
            groupClient,
            groupUpdateManager
        )
    }

    @GroupScope
    @Provides
    fun provideGroupUpdateManager(context: Context, sessionManager: SessionManager) : GroupUpdateManager {
        return GroupUpdateManager(context, sessionManager)
    }

    @GroupScope
    @Provides
    fun provideMeetingNoteRepository(groupDao: GroupDao, groupClient: GroupClient): MeetingNoteRepository {
        return MeetingNoteRepository(groupDao, groupClient)
    }
}