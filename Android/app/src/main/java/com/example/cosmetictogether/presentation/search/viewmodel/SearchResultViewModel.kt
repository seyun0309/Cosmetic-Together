package com.example.cosmetictogether.presentation.search.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmetictogether.data.model.FormSummaryResponse
import com.example.cosmetictogether.data.model.PostRecentResponse
import kotlinx.coroutines.launch

class SearchResultViewModel(private val repository: SearchRepository) : ViewModel() {

    private val _postList = MutableLiveData<List<PostRecentResponse>>()
    val postList: LiveData<List<PostRecentResponse>> get() = _postList

    private val _formList = MutableLiveData<List<FormSummaryResponse>>()
    val formList: LiveData<List<FormSummaryResponse>> get() = _formList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getPostByKeyword(keyword: String) {
        viewModelScope.launch {
            try {
                val posts = repository.getPostByKeyword(keyword)
                _postList.value = posts
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun getFormByKeyword(keyword: String) {
        viewModelScope.launch {
            try {
                val forms = repository.getFormByKeyword(keyword)
                _formList.value = forms
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}