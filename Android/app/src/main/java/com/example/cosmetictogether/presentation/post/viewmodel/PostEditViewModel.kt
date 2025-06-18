package com.example.cosmetictogether.presentation.post.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cosmetictogether.presentation.post.adapter.PostRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class PostEditViewModel(private val repository: PostRepository) : ViewModel() {

    private val _postDescription = MutableLiveData<String>()
    val postDescription: LiveData<String> get() = _postDescription

    private val _postImages = MutableLiveData<List<String>>()
    val postImages: LiveData<List<String>> get() = _postImages

    private val _imageFiles = MutableLiveData<List<File>>(emptyList())
    val images: LiveData<List<File>> get() = _imageFiles

    private val _deletedImages = mutableListOf<String>()

    fun initializePostData(description: String, images: List<String>) {
        _postDescription.value = description
        _postImages.value = images
    }

    fun removeImage(imageUrl: String) {
        _postImages.value = _postImages.value?.filterNot { it == imageUrl }
        _deletedImages.add(imageUrl)
    }

    fun addImage(file: File) {
        _imageFiles.value = _imageFiles.value?.toMutableList()?.apply { add(file) }
        _postImages.value = _postImages.value.orEmpty().toMutableList().apply { add(file.absolutePath) }
    }

    fun updatePostDescription(description: String) {
        _postDescription.value = description
    }

    fun updatePostImages(images: List<String>) {
        _postImages.value = images
    }

    fun deleteImageAt(imageUrl: String) {
        _postImages.value = _postImages.value?.filterNot { it == imageUrl }
        _deletedImages.add(imageUrl)
    }

    fun editPost(boardId: Long, token: String, description: String, deletedImageUrls: List<String>, callback: (Boolean) -> Unit) {
        val imageParts = _imageFiles.value?.mapIndexed { index, file ->
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images", "image$index.jpg", requestBody)
        } ?: emptyList()

        viewModelScope.launch {
            try {
                val json = JSONObject().apply {
                    put("description", description)
                    put("deleteImageUrls", JSONArray(deletedImageUrls))
                }.toString()

                val textRequestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val response = repository.postEdit(boardId, token, textRequestBody, imageParts)

                callback(response.isSuccessful)
            } catch (e: Exception) {
                Log.e("PostEditViewModel", "editPost error", e)
                callback(false)
            }
        }
    }

    fun getDeletedImageUrls(): List<String> {
        return _deletedImages
    }
}