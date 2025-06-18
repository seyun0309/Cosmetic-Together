package com.example.cosmetictogether.presentation.search.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.databinding.ActivitySearchResultBinding
import com.example.cosmetictogether.presentation.form.adapter.FormAdapter
import com.example.cosmetictogether.presentation.post.adapter.PostAdapter
import com.example.cosmetictogether.presentation.post.view.PostDetailActivity
import com.example.cosmetictogether.presentation.search.viewmodel.SearchRepository
import com.example.cosmetictogether.presentation.search.viewmodel.SearchResultViewModel
import com.example.cosmetictogether.presentation.search.viewmodel.SearchResultViewModelFactory
import com.google.android.material.tabs.TabLayout

class SearchResultActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySearchResultBinding
    private val viewModel : SearchResultViewModel by viewModels {
        SearchResultViewModelFactory(SearchRepository(RetrofitClient.apiSearch))
    }

    private lateinit var postAdapter: PostAdapter
    private lateinit var formAdapter: FormAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel;
        binding.lifecycleOwner = this

        val keyword = intent.getStringExtra("keyword") ?: ""

        // LayoutManager 설정
        binding.searchResultRecyclerView.layoutManager = LinearLayoutManager(this)

        // RecyclerView 초기화
        postAdapter = PostAdapter { post ->
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("boardId", post.boardId)
            startActivity(intent)
        }
        formAdapter = FormAdapter()

        // 기본은 게시글 리스트로 설정
        binding.searchResultRecyclerView.adapter = postAdapter
        viewModel.getPostByKeyword(keyword)

        viewModel.postList.observe(this) { posts ->
            postAdapter.submitList(posts)
        }

        viewModel.formList.observe(this) { forms ->
            formAdapter.submitList(forms)
        }

        binding.postTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.searchResultRecyclerView.adapter = postAdapter
                        viewModel.getPostByKeyword(keyword)
                    }
                    1 -> {
                        binding.searchResultRecyclerView.adapter = formAdapter
                        viewModel.getFormByKeyword(keyword)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}