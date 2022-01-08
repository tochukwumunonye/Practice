package com.tochukwu.practice.ui.article

import androidx.lifecycle.*
import com.tochukwu.practice.model.Article
import com.tochukwu.practice.repository.NewsRepository
import com.tochukwu.practice.util.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ArticleViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel(){

    private val _articleId = MutableLiveData<String>()
    private val _shareArticleEvent = MutableLiveData<SingleEvent<String>>()
    private val _openWebsiteEvent = MutableLiveData<SingleEvent<String>>()

    val openWebsiteEvent: LiveData<SingleEvent<String>>
        get() = _openWebsiteEvent


    val shareArticleEvent: LiveData<SingleEvent<String>>
        get() = _shareArticleEvent




    val articleLiveData: LiveData<Article> = _articleId.switchMap{id->
        repository.getArticle(id).asLiveData()
    }

    fun fetchArticle(id: String){
        _articleId.value = id
    }

    fun shareArticle(articleUrl: String){
        _shareArticleEvent.value = SingleEvent(articleUrl)
    }

    fun openWebsite(articleUrl:String){
        _openWebsiteEvent.value = SingleEvent(articleUrl)
    }

}

