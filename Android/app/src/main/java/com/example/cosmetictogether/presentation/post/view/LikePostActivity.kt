package com.example.cosmetictogether.presentation.post.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.databinding.ActivityLikePostBinding
import com.example.cosmetictogether.presentation.form.view.FormActivity
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity
import com.example.cosmetictogether.presentation.post.adapter.LikePostRepository
import com.example.cosmetictogether.presentation.post.adapter.PostAdapter
import com.example.cosmetictogether.presentation.post.viewmodel.LikePostViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.LikePostViewModelFactory

class LikePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLikePostBinding
    private lateinit var postAdapter: PostAdapter
    private val viewModel: LikePostViewModel by viewModels {
        LikePostViewModelFactory(LikePostRepository(RetrofitClient.postApi))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 초기화
        postAdapter = PostAdapter { post ->
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("boardId", post.boardId)
            startActivity(intent)
        }

        binding.postRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.postRecyclerView.adapter = postAdapter

        // 게시글 목록 불러오기
        viewModel.fetchPosts(getToken())

        // 게시글 목록 관찰
        viewModel.postList.observe(this) { posts ->
            postAdapter.submitList(posts.reversed())
        }

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 하단 네비게이션 바 설정
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.cosmetictogether.R.id.action_mypage -> {
                    startActivity(Intent(this, MyPageActivity::class.java))
                    true
                }
                com.example.cosmetictogether.R.id.action_form -> {
                    startActivity(Intent(this, FormActivity::class.java))
                    true
                }
                com.example.cosmetictogether.R.id.action_home -> {
                    true
                }
                com.example.cosmetictogether.R.id.action_search -> true
                else -> false
            }
        }
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        val authToken = "Bearer $token"
        return authToken
    }
}