package com.example.cosmetictogether.presentation.post.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cosmetictogether.R
import com.example.cosmetictogether.databinding.ActivityPostDetailBinding
import com.example.cosmetictogether.presentation.form.view.FormActivity
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity
import com.example.cosmetictogether.presentation.post.viewmodel.PostDetailViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.PostDetailViewModelFactory
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.presentation.post.adapter.CommentAdapter
import com.example.cosmetictogether.presentation.post.adapter.PostDetailRepository
import com.example.cosmetictogether.presentation.search.view.SearchActivity

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private val viewModel: PostDetailViewModel by viewModels {
        PostDetailViewModelFactory(
            PostDetailRepository(
                apiService = RetrofitClient.postApi,
                commentApi = RetrofitClient.commentApi
            )
        )
    }

    private var boardId: Long = -1L
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId = intent.getLongExtra("boardId", -1L)
        if (boardId == -1L) {
            Toast.makeText(this, "게시글 ID를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        token = getToken()

        setupObservers()
        setupBottomNavigationView()

        viewModel.fetchPostDetail(token, boardId.toString())

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 좋아요 클릭
        binding.favoriteIcon.setOnClickListener {
            viewModel.toggleLike(boardId, token)
        }

        // 팔로우 or 언팔로우 클릭
        binding.followButton.setOnClickListener {
            viewModel.toggleFollow(boardId, token)
        }

        // 댓글 작성 버튼
        binding.createPostButton.setOnClickListener {
            val comment = binding.commentEditText.text.toString()

            viewModel.postComment(token, boardId, comment,
                onSuccess = {
                    Toast.makeText(this, "댓글이 등록되었습니다", Toast.LENGTH_SHORT).show()
                    binding.commentEditText.setText("")  // 입력 필드 초기화

                    viewModel.fetchPostDetail(token, boardId.toString())
                },
                onFailure = {
                    Toast.makeText(this, "댓글 등록 실패", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.commentRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: android.view.View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.top = 12  // 아이템 위 간격
                outRect.bottom = 12  // 아이템 아래 간격
            }
        })
    }

    private fun setupObservers() {
        viewModel.postDetail.observe(this) { postDetail ->
            postDetail?.let {
                binding.post = it
                binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.commentRecyclerView.adapter = CommentAdapter(it.comments)

                // 게시글 사진
                val boardUrls = it.boardUrl
                val imageViews = listOf(binding.image1, binding.image2, binding.image3, binding.image4)

                if (boardUrls.isNullOrEmpty()) {
                    binding.imageGrid.visibility = View.GONE
                } else {
                    binding.imageGrid.visibility = View.VISIBLE

                    imageViews.forEachIndexed { index, imageView ->
                        if (index < boardUrls.size) {
                            imageView.visibility = View.VISIBLE
                            Glide.with(this)
                                .load(boardUrls[index])
                                .into(imageView)
                        } else {
                            imageView.visibility = View.GONE
                        }
                    }
                }

                // 좋아요 아이콘 설정
                val isLiked = it.isLiked
                binding.favoriteIcon.setImageResource(
                    if (isLiked) R.drawable.baseline_favorite_red_24
                    else R.drawable.baseline_favorite_border_24
                )

                val isFollowed = it.following
                binding.followButton.apply {
                    text = if (isFollowed) "언팔로우" else "팔로우"
                    setBackgroundResource(
                        if (isFollowed) R.drawable.baseline_unfollow
                        else R.drawable.baseline_follow
                    )
                }

                // 댓글 리사이클러뷰 설정
                binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.commentRecyclerView.adapter = CommentAdapter(it.comments)
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.deleteStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "게시글 삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.post_detail_menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPostDetail(getToken(), boardId.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_POST && resultCode == RESULT_OK) {
            viewModel.fetchPostDetail(token, boardId.toString())
            Toast.makeText(this, "글이 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigationView() {
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
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }
                R.id.action_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }

                else -> {true}
            }
        }
    }

    companion object {
        private const val REQUEST_EDIT_POST = 1001
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        val authToken = "Bearer $token"
        return authToken
    }
}