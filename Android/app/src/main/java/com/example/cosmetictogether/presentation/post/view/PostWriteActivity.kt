package com.example.cosmetictogether.presentation.post.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cosmetictogether.databinding.ActivityPostWriteBinding
import com.example.cosmetictogether.presentation.post.viewmodel.PostWriteViewModel
import com.example.cosmetictogether.presentation.post.viewmodel.PostWriteViewModelFactory
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.presentation.post.adapter.PostImageAdapter
import com.example.cosmetictogether.presentation.post.adapter.PostRepository
import java.io.File
import java.io.FileOutputStream

class PostWriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostWriteBinding
    private lateinit var adapter: PostImageAdapter
    private val viewModel: PostWriteViewModel by viewModels {
        PostWriteViewModelFactory(application, PostRepository(RetrofitClient.postApi))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // 취소 버튼: PostActivity로 이동
        binding.toolbar.findViewById<ImageButton>(com.example.cosmetictogether.R.id.backBtn).setOnClickListener {
            startActivity(Intent(this, PostActivity::class.java))
            finish()
        }

        // 완료 버튼: 서버로 전송 후 PostActivity로 이동
        binding.completeButton.setOnClickListener {
            val description = binding.postEditTex.text.toString()

            if (description.isNullOrBlank()) {
                Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.uploadPost(description) { success ->
                if (success) {
                    Toast.makeText(this, "게시글이 작성되었습니다", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, PostActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "작성 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 이미지 RecyclerView 설정
        adapter = PostImageAdapter{ imagePath ->
            viewModel.removeImage(imagePath)
        }

        binding.imageRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.imageRecyclerView.adapter = adapter

        viewModel.postImages.observe(this) { images ->
            // File 리스트를 String 경로 리스트로 변환해서 전달
            val imagePaths = images.map { it.absolutePath }
            adapter.submitList(imagePaths)
        }

        // 이미지 추가 버튼
        binding.imageSelectButton.setOnClickListener {
            if ((viewModel.postImages.value?.size ?: 0) >= 4) {
                Toast.makeText(this, "이미지는 최대 4개까지 업로드 가능합니다", Toast.LENGTH_SHORT).show()
            } else {
                imagePicker.launch("image/*")
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
}