package com.tochukwu.practice.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.paging.ExperimentalPagingApi
import com.tochukwu.practice.MainCoroutinesRule
import com.tochukwu.practice.getOrAwaitValue
import com.tochukwu.practice.repository.NewsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.hamcrest.CoreMatchers.`is`


@ExperimentalCoroutinesApi
@ExperimentalPagingApi
class ArticleListViewModelTest{
    @get:Rule
    var coroutinesRule = MainCoroutinesRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: NewsRepository

    private lateinit var viewModel: ArticleListViewModel

    @Before
    fun setUp() {
        repository = Mockito.mock(NewsRepository::class.java)
        viewModel = ArticleListViewModel(repository, SavedStateHandle())
    }

    @Test
    fun updateCategoryTest(){
        viewModel.updateCategory("sport", 0)
        val value = viewModel.categoryLiveData.getOrAwaitValue()
        assertThat(value, `is` ("sport"))

    }

    @Test
    fun updateLanguageTest(){
        viewModel.updateLanguage("gb")
        val value = viewModel.languageLiveData.getOrAwaitValue()
        assertThat(value, `is` ("gb"))
    }


    @Test
    fun saveCategoryGetSavedCategory() {
        viewModel.saveCategoryFiltering("sport")
        val value = viewModel.getLastSavedCategory()
        assertThat(value, `is`("sport"))
    }

    @Test
    fun saveLanguageGetSavedLanguage() {
        viewModel.saveLanguageFiltering("gb")
        val value = viewModel.getLastSavedLanguage()
        assertThat(value, `is`("gb"))

    }

}