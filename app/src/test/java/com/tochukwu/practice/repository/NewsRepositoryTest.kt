package com.tochukwu.practice.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tochukwu.practice.MainCoroutinesRule
import com.tochukwu.practice.TestUtil
import com.tochukwu.practice.api.NewsAPI
import com.tochukwu.practice.db.ArticleDao
import com.tochukwu.practice.db.NewsDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


@ExperimentalCoroutinesApi
class NewsRepositoryTest{

    @get:Rule
    val mainCoroutineRule = MainCoroutinesRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: NewsRepository
    private lateinit var database: NewsDatabase
    private lateinit var api : NewsAPI

    @Before
    fun setUp(){
        database = mock(NewsDatabase::class.java)
        api = mock(NewsAPI::class.java)
        repository = NewsRepository(api, database)
    }

    @Test
    fun getArticleFlow() = runBlocking{
        //stub out database to return mock dao
        val dao = mock(ArticleDao::class.java)

        `when`(database.articleDao()).thenReturn(dao)
        //stub out dao to return article

        val article = TestUtil.createArticle()

        `when`(dao.getNewsById("TEST_ID")).thenReturn(article)


        val flow = repository.getArticle("TEST_ID")

        flow.collect{result ->
            assertThat(result, `is` (article))
        }
    }

}

