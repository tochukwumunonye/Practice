package com.tochukwu.practice.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.tochukwu.practice.api.NewsAPI
import com.tochukwu.practice.api.NewsResponse
import com.tochukwu.practice.api.asModel
import com.tochukwu.practice.db.NewsDatabase
import com.tochukwu.practice.db.RemoteKey
import com.tochukwu.practice.model.Article
import com.tochukwu.practice.util.EspressoUriIdlingResource
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.io.InvalidObjectException
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalPagingApi
@Singleton
class NewsRemoteMediator @Inject constructor(

    private val language: String,
    private val category: String,
    private val api: NewsAPI,
    private val database: NewsDatabase
) : RemoteMediator<Int, Article>() {

    private val remoteKeyDao = database.remoteKeyDao()
    private val articleKeyDao = database.articleDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {

        try {
            val loadKey: Int = when (loadType) {
                LoadType.REFRESH -> {
                    Timber.i("REFRESH")
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE
                }
                LoadType.PREPEND -> {
                    Timber.i("Prepend")
                    val remoteKey = getRemoteKeyForFirstItem(state)
                        ?: throw InvalidObjectException("Something went wrong.")
                    remoteKey.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKey.prevKey
                }
                LoadType.APPEND -> {
                    Timber.i("append")
                    val remoteKey = getRemoteKeyForLastItem(state)
                    if (remoteKey?.nextKey == null) throw InvalidObjectException("something went wrong")
                    remoteKey.nextKey
                }
            }

            val apiResponse = newsResponse(loadKey, state)
            val news = apiResponse.asModel()
            val endOfPaginationReached = news.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.clearRemoteKeys()
                    articleKeyDao.clearNews()
                }
                val prevKey = if (loadKey == STARTING_PAGE) null else loadKey - 1
                val nextKey = if (endOfPaginationReached) null else loadKey + 1
                val keys = news.map { article ->
                    RemoteKey(articleId = article.id, nextKey = nextKey, prevKey = prevKey)
                }

                for (article in news) {
                    article.language = language
                    article.category = category
                }
                articleKeyDao.insertAll(news)
                remoteKeyDao.insertAll(keys)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun  getRemoteKeyForFirstItem(state: PagingState<Int, Article>): RemoteKey?{
        return state.pages.firstOrNull(){
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { firstArticle->
            remoteKeyDao.remoteKeyByArticle(firstArticle.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Article>): RemoteKey? {
        return state.pages.lastOrNull() {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let {lastArticle ->
            remoteKeyDao.remoteKeyByArticle(lastArticle.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Article>): RemoteKey?{
        return state.anchorPosition?.let{position ->
            state.closestItemToPosition(position)?.id?.let{ articleId ->
                remoteKeyDao.remoteKeyByArticle(articleId)
            }
        }
    }

    private suspend fun newsResponse(loadKey: Int, state: PagingState<Int, Article>): NewsResponse {
        EspressoUriIdlingResource.beginLoad()
        try{
            return api.getNews(
                country = language,
                category = category,
                page = loadKey,
                pageSize = state.config.pageSize
            )
        } finally{
            EspressoUriIdlingResource.endLoad()
        }

    }
    companion object {
        private const val STARTING_PAGE = 1
    }
}







