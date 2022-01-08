package com.tochukwu.practice.di

import android.app.Application
import androidx.room.Room
import com.tochukwu.practice.db.ArticleDao
import com.tochukwu.practice.db.NewsDatabase
import com.tochukwu.practice.db.RemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): NewsDatabase {
        return Room
            .databaseBuilder(app, NewsDatabase::class.java, "newsster_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideArticleDao(newssterDatabase: NewsDatabase): ArticleDao {
        return newssterDatabase.articleDao()
    }

    @Provides
    fun provideRemoteKeysDao(newssterDatabase: NewsDatabase): RemoteKeyDao {
        return newssterDatabase.remoteKeyDao()
    }
}