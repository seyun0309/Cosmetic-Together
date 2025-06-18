package com.example.cosmetictogether.presentation.post.view

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.databinding.ActivityPostEditBinding
import com.example.cosmetictogether.presentation.post.adapter.PostImageAdapter
import com.example.cosmetictogether.presentation.post.adapter.PostRepository
import com.example.cosmetictogether.presentation.post.viewmodel.PostEditViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.PostEditViewModelFactory
import java.io.File
import java.io.FileOutputStream

class PostEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostEditBinding
    private lateinit var adapter: PostImageAdapter
    private val viewModel: PostEditViewModel by viewModels{
        PostEditViewModelFactory(PostRepository(RetrofitClient.postApi))
    }

    private var boardId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 데이터 바인딩
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        boardId = intent.getLongExtra("boardId", -1L)
        if (boardId == -1L) {
            Toast.makeText(this, "게시글 ID를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 취소 버튼
        binding.backButton.setOnClickListener {
            finish()
        }

        // 게시글 정보 받아오기
        val postDescription = intent.getStringExtra("postDescription") ?: ""
        val postImages = intent.getStringArrayListExtra("postImages") ?: arrayListOf()
        viewModel.initializePostData(postDescription, postImages)

        // 이미지 어댑터 및 RecyclerView 설정
        adapter = PostImageAdapter(onDeleteClick = { imageUrl ->
            viewModel.removeImage(imageUrl)
        })
        binding.imageRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.imageRecyclerView.adapter = adapter

        // LiveData 관찰하여 RecyclerView 갱신
        viewModel.postImages.observe(this) { imageList ->
            adapter.submitList(imageList.toList())
        }

        // 이미지 추가 버튼
        binding.imageSelectButton.setOnClickListener {
            if ((viewModel.postImages.value?.size ?: 0) >= 4) {
                Toast.makeText(this, "이미지는 최대 4개까지 업로드 가능합니다", Toast.LENGTH_SHORT).show()
            } else {
                imagePicker.launch("image/*")
            }
        }

        // 완료 버튼 클릭 시 수정 완료 처리
        binding.completeButton.setOnClickListener {
            val description = binding.postEditText.text.toString()

            if (description.isNullOrBlank()) {
                Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val deletedImages = viewModel.getDeletedImageUrls()

            viewModel.editPost(boardId, getToken(), description, deletedImages) { success ->
                if (success) {
                    Toast.makeText(this, "게시글이 수정되었습니다", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(uri)
            file?.let {
                viewModel.addImage(it)
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val returnCursor = contentResolver.query(uri, null, null, null, null) ?: return null
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()

        val file = File(cacheDir, name)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        val authToken = "Bearer $token"
        return authToken
    }
}