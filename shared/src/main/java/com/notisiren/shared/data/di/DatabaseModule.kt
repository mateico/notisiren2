package com.notisiren.shared.data.di

import android.content.Context
import androidx.room.Room
import com.notisiren.shared.data.local.NotiSirenDataBase
import com.notisiren.shared.data.local.dao.FilterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NotiSirenDataBase =
        Room.databaseBuilder(
            context,
            NotiSirenDataBase::class.java,
            "notisiren.db"
        ).build()
    @Provides
    fun provideFilterDao(database: NotiSirenDataBase): FilterDao =
        database.filterDao()

}