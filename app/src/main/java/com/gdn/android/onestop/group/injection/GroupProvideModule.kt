package com.gdn.android.onestop.group.injection

import android.app.Application
import androidx.room.Room
import com.gdn.android.onestop.group.data.*
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
    fun provideGroupRepository(groupDao: GroupDao, groupClient: GroupClient): GroupRepository {
        return GroupRepository(groupDao, groupClient)
    }

    @GroupScope
    @Provides
    fun provideGroupChatRepository(groupDao: GroupDao, groupClient: GroupClient) : GroupChatRepository {
        return GroupChatRepository(groupDao, groupClient)
    }
}