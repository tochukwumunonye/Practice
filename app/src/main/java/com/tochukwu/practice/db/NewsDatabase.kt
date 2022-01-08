package com.tochukwu.practice.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tochukwu.practice.model.Article
import com.tochukwu.practice.model.SourceConverter


@Database(entities = [Article::class, RemoteKey::class], version = 1, exportSchema = false)
@TypeConverters(value = [SourceConverter::class])
abstract class NewsDatabase : RoomDatabase(){
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}


