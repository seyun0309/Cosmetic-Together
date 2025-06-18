package com.example.cosmetictogether.presentation.post.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.R
import com.example.cosmetictogether.databinding.ActivityPostBinding
import com.example.cosmetictogether.presentation.form.view.FormActivity
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity
import com.example.cosmetictogether.presentation.post.adapter.PostAdapter
import com.example.cosmetictogether.presentation.post.adapter.PostRepository
import com.example.cosmetictogether.presentation.post.viewmodel.PostViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.PostViewModelFactory
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.presentation.search.view.SearchActivity
import com.google.android.material.tabs.TabLayout

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var postAdapter: PostAdapter
    private val viewModel: PostViewModel by viewModels {
        PostViewModelFactory(PostRepository(RetrofitClient.postApi))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 초기화
        postAdapter = PostAdapter { post ->
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("boardId", post.boardId)
            startActivity(intent)
        }

        binding.postRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.postRecyclerView.adapter = postAdapter

        // 게시글 목록 관찰
        viewModel.postList.observe(this) { posts ->
            postAdapter.submitList(posts.reversed())
        }

        // 탭 클릭 리스너 설정
        binding.postTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val token = getToken()

                when (tab?.position) {
                    0 -> viewModel.fetchPosts()              // 최근 탭
                    1 -> viewModel.fetchFollowingPosts(token)       // 팔로잉 탭
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 게시글 목록 불러오기
        viewModel.fetchPosts()

        // 글 작성 버튼 클릭 시 이동
        binding.createPostButton.setOnClickListener {
            startActivity(Intent(this, PostWriteActivity::class.java))
        }

        binding.bottomNavigationView.selectedItemId = R.id.action_home

        // 하단 네비게이션 바 설정
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_mypage -> {
                    startActivity(Intent(this, MyPageActivity::class.java))
                    true
                }
                R.id.action_form -> {
                    startActivity(Intent(this, FormActivity::class.java))
                    true
                }
                R.id.action_home -> {
                    true
                }
                R.id.action_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
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