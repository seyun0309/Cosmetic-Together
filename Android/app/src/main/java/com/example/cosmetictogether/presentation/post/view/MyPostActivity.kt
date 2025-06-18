package com.example.cosmetictogether.presentation.post.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.databinding.ActivityMypostBinding
import com.example.cosmetictogether.presentation.form.view.FormActivity
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity
import com.example.cosmetictogether.presentation.post.adapter.PostAdapter
import com.example.cosmetictogether.presentation.post.adapter.PostRepository
import com.example.cosmetictogether.presentation.post.viewmodel.PostViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.PostViewModelFactory

class MyPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypostBinding
    private lateinit var postAdapter: PostAdapter
    private val viewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepository(RetrofitClient.postApi))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 초기화
        postAdapter = PostAdapter { post ->
            val intent = Intent(this, MyPostDetailActivity::class.java)
            intent.putExtra("boardId", post.boardId)
            startActivity(intent)
        }

        binding.postRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.postRecyclerView.adapter = postAdapter

        // 네비게이션바 마이페이지로 고정
        binding.bottomNavigationView.selectedItemId = R.id.action_mypage

        // 글 작성 버튼 클릭 시 이동
        binding.createPostButton.setOnClickListener {
            startActivity(Intent(this, PostWriteActivity::class.java))
        }

        // 게시글 목록 불러오기
        val token = getToken()
        viewModel.fetchMyPosts(token)

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