package com.example.cosmetictogether.presentation.post.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.databinding.ActivityMypostDetailBinding
import com.example.cosmetictogether.presentation.post.adapter.CommentAdapter
import com.example.cosmetictogether.presentation.post.adapter.PostDetailRepository
import com.example.cosmetictogether.presentation.post.viewmodel.PostDetailViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.PostDetailViewModelFactory

class MyPostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypostDetailBinding
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
        binding = ActivityMypostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId = intent.getLongExtra("boardId", -1L)
        if (boardId == -1L) {
            Toast.makeText(this, "게시글 ID를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        token = getToken()

        setupObservers()

        viewModel.fetchPostDetail(token, boardId.toString())

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 좋아요 클릭
        binding.favoriteIcon.setOnClickListener {
            viewModel.toggleLike(boardId, token)
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

        // 점 세개 클릭
        binding.moreOptions.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.item_menu_options, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        val intent = Intent(this, PostEditActivity::class.java).apply {
                            putExtra("boardId", boardId)
                            putExtra("postDescription", viewModel.postDetail.value?.description)
                            putStringArrayListExtra("postImages", ArrayList(viewModel.postDetail.value?.boardUrl ?: emptyList()))
                        }
                        startActivityForResult(intent, REQUEST_EDIT_POST)
                        true
                    }

                    R.id.action_delete -> {
                        showDeleteDialog()
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val intent = Intent(this, PostEditActivity::class.java).apply {
                    putExtra("boardId", boardId)
                    putExtra("postDescription", viewModel.postDetail.value?.description)
                    putStringArrayListExtra("postImages", ArrayList(viewModel.postDetail.value?.boardUrl ?: emptyList()))
                }
                startActivityForResult(intent, REQUEST_EDIT_POST)
                true
            }
            R.id.action_delete -> {
                showDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPostDetail(getToken(), boardId.toString())
    }

    private fun showDeleteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_post, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.yesButton).setOnClickListener {
            viewModel.deletePost(token, boardId)
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.noButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_POST && resultCode == RESULT_OK) {
            viewModel.fetchPostDetail(token, boardId.toString())
            Toast.makeText(this, "글이 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
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