package com.example.cosmetictogether.presentation.post.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.model.PostDetailResponse
import androidx.lifecycle.viewModelScope
import com.example.cosmetictogether.data.model.CommentRequest
import com.example.cosmetictogether.data.model.PostRecentResponse
import com.example.cosmetictogether.presentation.post.adapter.PostDetailRepository
import kotlinx.coroutines.launch

class PostDetailViewModel(private val repository: PostDetailRepository) : ViewModel() {

    private val _postDetail = MutableLiveData<PostDetailResponse?>()
    val postDetail: LiveData<PostDetailResponse?> get() = _postDetail

    private val _deleteStatus = MutableLiveData<Boolean>()
    val deleteStatus: LiveData<Boolean> get() = _deleteStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _commentText = MutableLiveData<String>()
    val commentText: LiveData<String> get() = _commentText

    private val _postList = MutableLiveData<List<PostRecentResponse>>()
    val postList: LiveData<List<PostRecentResponse>> get() = _postList

    fun fetchPostDetail(token: String, boardId: String) {
        repository.getPostDetail(token, boardId) { postDetail, error ->
            if (postDetail != null) {
                _postDetail.value = postDetail
            } else {
                _errorMessage.value = error?.message ?: "게시글 정보를 가져오는 데 실패했습니다."
            }
        }
    }

    fun deletePost(token: String, boardId: Long) {
        viewModelScope.launch {
            try {
                val success = repository.deletePost(token, boardId)
                _deleteStatus.value = success
                if (!success) {
                    _errorMessage.value = "게시글 삭제에 실패했습니다."
                }
            } catch (e: Exception) {
                _deleteStatus.value = false
                _errorMessage.value = e.message ?: "게시글 삭제 중 오류가 발생했습니다."
            }
        }
    }

    fun postComment(token: String, boardId: Long, comment: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val request = CommentRequest(boardId, comment)
        repository.postComment(token, request, {
            _commentText.value = "" // 댓글 입력칸 초기화
            onSuccess()
        }, {
            onFailure()
        })
    }

    // 좋아요
    fun toggleLike(boardId: Long, token: String) {
        viewModelScope.launch {
            val success = repository.toggleLike(boardId, token)
            if (success) {
                    fetchPostDetail(token, boardId.toString())
            }
        }
    }

    fun toggleFollow(boardId: Long, token: String) {
        viewModelScope.launch {
            val success = repository.toggleFollow(boardId, token)
            if(success) {
                fetchPostDetail(token, boardId.toString())
            }
        }
    }
}