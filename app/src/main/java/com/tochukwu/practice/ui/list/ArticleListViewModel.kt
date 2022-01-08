package com.tochukwu.practice.ui.list

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tochukwu.practice.model.Article
import com.tochukwu.practice.repository.NewsRepository
import com.tochukwu.practice.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



@ExperimentalPagingApi
@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){


    private val _categoryLiveData: MutableLiveData<String> = MutableLiveData(getLastSavedCategory())
    private val _languageLiveData: MutableLiveData<String> = MutableLiveData(getLastSavedLanguage())

    private val _categoryLocalizedLiveData: MutableLiveData<Int> =
        MutableLiveData(getLastSavedLocalizedCategory())

    val categoryLiveData: LiveData<String> get() = _categoryLiveData
    val languageLiveData: LiveData<String> get() = _languageLiveData



    val categoryLocalLiveData: LiveData<Int> get() = _categoryLocalizedLiveData



    private var currentNews: Flow<PagingData<Article>>? = null


    fun loadNews(): Flow<PagingData<Article>>{

        val category = _categoryLiveData.value!!
        val language = _languageLiveData.value!!

        val lastResult = currentNews
        if(lastResult != null && !shouldRefresh(language, category)) return lastResult

        val newNews = repository.fetchArticles(language, category).cachedIn(viewModelScope)
        currentNews = newNews


        //Save new filters after checks are made to establish, if the news should be refreshed.
        saveCategoryFiltering(category)
        saveLanguageFiltering(language)
        savedCategoryLocalize()
        return newNews


    }



    fun getLastSavedCategory() = savedStateHandle.getLiveData<String>(SAVED_STATE_CATEGORY).value?: DEFAULT_STATE_CATEGORY

    fun getLastSavedLanguage() = savedStateHandle.getLiveData<String>(SAVED_STATE_LANGUAGE).value?: DEFAULT_LANGUAGE

    private fun shouldRefresh(language: String, category: String): Boolean {
        return category != getLastSavedCategory() || language != getLastSavedLanguage()
    }

    fun saveCategoryFiltering(category: String){
        savedStateHandle.set(SAVED_STATE_CATEGORY, category)
    }

    fun saveLanguageFiltering(language: String) {
        savedStateHandle.set(SAVED_STATE_LANGUAGE, language)
    }

    private fun savedCategoryLocalize(){
        _categoryLocalizedLiveData.value?.let{
            savedStateHandle.set(SAVED_STATE_LOCAL_TITLE, it)
        }
    }


    private fun getLastSavedLocalizedCategory() = savedStateHandle.getLiveData<Int>(
        SAVED_STATE_LOCAL_TITLE).value ?: DEFAULT_TITLE

    fun updateCategory(category: String, categoryLocalized:Int){
        _categoryLiveData.value = category
        _categoryLocalizedLiveData.value = categoryLocalized
    }


    fun updateLanguage(language: String) {
        _languageLiveData.value = language
    }


}




