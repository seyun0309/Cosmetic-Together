package com.example.cosmetictogether.presentation.form.viewmodel

import com.example.cosmetictogether.data.api.FormRetrofitInterface
import retrofit2.awaitResponse

class FormDetailRepository(private val apiForm: FormRetrofitInterface) {

    // 찜 클릭
    suspend fun toggleFavorite(formId: Long, token: String): Boolean {
        val response = apiForm.favoriteOrUnfavoriteForm(token, formId).awaitResponse()
        return response.isSuccessful
    }

    suspend fun toggleFollow(formId: Long, token: String): Boolean {
        val response = apiForm.followOrUnfollow(token, formId).awaitResponse()
        return response.isSuccessful
    }

    fun deletePost(token: String, formId: Long): Boolean {
        return try {
            val response = apiForm.postDelete(token, formId).execute()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}