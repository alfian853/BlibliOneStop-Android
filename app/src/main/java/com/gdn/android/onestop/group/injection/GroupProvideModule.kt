package com.gdn.android.onestop.group.injection

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.gdn.android.onestop.group.data.*
import com.gdn.android.onestop.util.SessionManager
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
    fun provideGroupDatabase(application: Application) : GroupDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            GroupDatabase::class.java, "group_database"
        ).fallbackToDestructiveMigration().build()
    }

    @GroupScope
    @Provides
    fun provideGroupDao(groupDatabase: GroupDatabase) : GroupDao {
        return groupDatabase.GroupDao()
    }

    @GroupScope
    @Provides
    fun provideGroupRepository(groupDao: GroupDao, groupClient: GroupClient, groupUpdateManager: GroupUpdateManager): GroupRepository {
        return GroupRepository(groupDao, groupClient, groupUpdateManager)
    }

    @GroupScope
    @Provides
    fun provideGroupChatRepository(groupDao: GroupDao, groupClient: GroupClient, sessionManager: SessionManager) : GroupChatRepository {
        return GroupChatRepository(groupDao, groupClient, sessionManager)
    }

    @GroupScope
    @Provides
    fun provideGroupUpdateManager(context: Context, sessionManager: SessionManager) : GroupUpdateManager {
        return GroupUpdateManager(context, sessionManager)
    }
//
//    @GroupScope
//    @Provides
//    fun provideChatSocketClient(groupDao: GroupDao, sessionManager: SessionManager): ChatSocketClient {
//        return ChatSocketClient(groupDao, sessionManager,)
//    }
}