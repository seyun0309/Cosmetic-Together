package com.example.cosmetictogether.presentation.search.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cosmetictogether.databinding.ActivitySearchBinding
import com.example.cosmetictogether.presentation.search.adapter.RecentSearchAdapter
import com.example.cosmetictogether.presentation.search.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchBinding
    private val viewModel : SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel;
        binding.lifecycleOwner = this

        binding.recentSearchRecyclerView.adapter = RecentSearchAdapter(listOf("향수", "쿠션", "립밤"))
        binding.recentSearchRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val keyword = binding.searchEditText.text.toString().trim()
                Log.d("keyword : " , keyword)
                if (keyword.isNotEmpty()) {
                    val intent = Intent(this, SearchResultActivity::class.java)
                    intent.putExtra("keyword", keyword)
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.clearBtn.setOnClickListener {
            finish()
        }
    }
}