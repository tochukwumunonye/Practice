package com.tochukwu.practice.di

import com.tochukwu.practice.api.NewsAPI
import com.tochukwu.practice.db.NewsDatabase
import com.tochukwu.practice.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNewsRepository(
        service: NewsAPI,
        database: NewsDatabase
    ): NewsRepository {
        return NewsRepository(service, database)
    }
}