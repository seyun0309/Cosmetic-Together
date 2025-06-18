package com.example.cosmetictogether.presentation.post.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cosmetictogether.presentation.post.adapter.PostRepository

class PostWriteViewModelFactory(
    private val application: Application,
    private val repository: PostRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostWriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostWriteViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}