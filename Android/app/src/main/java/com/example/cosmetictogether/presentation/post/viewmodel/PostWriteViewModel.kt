package com.example.cosmetictogether.presentation.post.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cosmetictogether.presentation.post.adapter.PostRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class PostWriteViewModel(
    application: Application,
    private val repository: PostRepository
) : AndroidViewModel(application) {

    private val _postDescription = MutableLiveData<String>()
    val postDescription: LiveData<String> get() = _postDescription

    private val _imageFiles = MutableLiveData<List<File>>(emptyList())
    val postImages: LiveData<List<File>> get() = _imageFiles

    fun setPostDescription(description: String) {
        _postDescription.value = description
    }

    fun addImage(file: File) {
        _imageFiles.value = _imageFiles.value?.toMutableList()?.apply { add(file) }
    }

    fun removeImage(path: String) {
        _imageFiles.value = _imageFiles.value?.filterNot { it.absolutePath == path }
    }

    fun uploadPost(description: String, callback: (Boolean) -> Unit) {
        val token = getToken()
        val imageParts = _imageFiles.value?.mapIndexed { index, file ->
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images", "image$index.jpg", requestBody)
        } ?: emptyList()

        viewModelScope.launch {
            try {
                val json = JSONObject().apply {
                    put("description", description)
                }.toString()

                val textRequestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val response = repository.postWrite(token, textRequestBody, imageParts)
                callback(response.isSuccessful)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    private fun getToken(): String {
        val sharedPreferences = getApplication<Application>()
            .getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return "Bearer $token"
    }
}