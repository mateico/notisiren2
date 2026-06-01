package com.notisiren.shared.data.di

import com.notisiren.shared.data.repository.FilterRepositoryImpl
import com.notisiren.shared.domain.repository.FilterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFilterRepository(impl: FilterRepositoryImpl): FilterRepository
}