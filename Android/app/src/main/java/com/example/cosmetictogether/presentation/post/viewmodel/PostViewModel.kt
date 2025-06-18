package com.example.cosmetictogether.presentation.post.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmetictogether.data.model.PostRecentResponse
import com.example.cosmetictogether.presentation.post.adapter.LikePostRepository
import com.example.cosmetictogether.presentation.post.adapter.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    private val _postList = MutableLiveData<List<PostRecentResponse>>()
    val postList: LiveData<List<PostRecentResponse>> get() = _postList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val posts = repository.getRecentPosts()
                _postList.value = posts
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun fetchMyPosts(token: String) {
        viewModelScope.launch {
            try {
                val posts = repository.getMyPosts(token)
                _postList.value = posts
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }


    fun fetchFollowingPosts(token: String) {
        viewModelScope.launch {
            try {
                val posts = repository.getFollowingPosts(token)
                _postList.value = posts
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}