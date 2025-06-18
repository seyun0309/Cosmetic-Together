package com.example.cosmetictogether.presentation.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cosmetictogether.presentation.post.adapter.LikePostRepository

class LikePostViewModelFactory(private val repository: LikePostRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LikePostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LikePostViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}