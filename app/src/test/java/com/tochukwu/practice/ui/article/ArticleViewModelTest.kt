package com.tochukwu.practice.ui.article

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tochukwu.practice.MainCoroutinesRule
import com.tochukwu.practice.TestUtil
import com.tochukwu.practice.api.NewsAPI
import com.tochukwu.practice.db.NewsDatabase
import com.tochukwu.practice.getOrAwaitValue
import com.tochukwu.practice.repository.NewsRepository
import org.hamcrest.CoreMatchers.`is`
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ArticleViewModelTest{

    @get:Rule
    var coroutineRule = MainCoroutinesRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var repository: NewsRepository
    private lateinit var viewModel: ArticleViewModel

    private val article = TestUtil.createArticle()
    private val database = mock(NewsDatabase::class.java, RETURNS_DEEP_STUBS)
    private val api = mock(NewsAPI::class.java)

    @Before
    fun setup(){
        repository = NewsRepository(api, database)
        viewModel = ArticleViewModel(repository)

    }


    @Test
    fun fetchArticleTest() = runBlockingTest {
        `when`(database.articleDao().getNewsById("TEST_ID")).thenReturn(article)
        viewModel.fetchArticle(id = "TEST_ID")

        val value = viewModel.articleLiveData.getOrAwaitValue()
        MatcherAssert.assertThat(value.id, `is`(article.id))
        MatcherAssert.assertThat(value.title, `is`(article.title))
        MatcherAssert.assertThat(value.description, `is`(article.description))
        MatcherAssert.assertThat(value.author, `is`(article.author))
        MatcherAssert.assertThat(value.url, `is`(article.url))
        MatcherAssert.assertThat(value.source.name, `is`(article.source.name))
        MatcherAssert.assertThat(value.imgUrl, `is`(article.imgUrl))
        MatcherAssert.assertThat(value.category, `is`(article.category))
        MatcherAssert.assertThat(value.language, `is`(article.language))
        MatcherAssert.assertThat(value.date, `is`(article.date))
    }

    @Test
    fun shareArticleTest(){
        viewModel.shareArticle(article.url)
        val event = viewModel.shareArticleEvent.getOrAwaitValue()
        MatcherAssert.assertThat(event.getContentIfNotHandled(), `is` (article.url))
    }

    @Test
    fun openWebsiteTest(){
        viewModel.openWebsite(article.url)
        val event = viewModel.openWebsiteEvent.getOrAwaitValue()
        MatcherAssert.assertThat(event.getContentIfNotHandled(), `is` (article.url))
    }

}


