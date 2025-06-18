package com.example.cosmetictogether.presentation.mypage.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.api.MyPageRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.databinding.ActivityMypageBinding
import com.example.cosmetictogether.presentation.form.view.FavoriteFormActivity
import com.example.cosmetictogether.presentation.form.view.FormActivity
import com.example.cosmetictogether.presentation.form.view.MyFormActivity
import com.example.cosmetictogether.presentation.login.LoginActivity
import com.example.cosmetictogether.presentation.mypage.viewmodel.MyPageViewModel
import com.example.cosmetictogether.presentation.order.view.OrderActivity
import com.example.cosmetictogether.presentation.post.view.LikePostActivity
import com.example.cosmetictogether.presentation.post.view.MyPostActivity
import com.example.cosmetictogether.presentation.post.view.PostActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private val viewModel: MyPageViewModel by viewModels()

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return "Bearer $token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val token = getToken()

        //마이페이지 화면에서 사용자 프로필 사진, 이름 띄우는 거
        viewModel.loadUserData(token)

        // 프로필 사진 변경
        binding.imgLogo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
        }

        // 로그아웃 클릭
        binding.logOutButton.setOnClickListener {

            // SharedPreferences에서 토큰 삭제
            val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("access_token")
            editor.apply()

            // 사용자를 로그인 화면으로 리디렉션
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 모든 Activity 스택을 제거
            startActivity(intent)
            finish()
        }

        // 팔로워 클릭
        binding.followerCount.setOnClickListener {
            val intent = Intent(this, FollowFollowingListActivity::class.java)
            intent.putExtra("tab", "follower")
            startActivity(intent)
        }

        // 팔로잉 클릭
        binding.followingCount.setOnClickListener {
            val intent = Intent(this, FollowFollowingListActivity::class.java)
            intent.putExtra("tab", "following")
            startActivity(intent)
        }

        // 판매 내역 버튼 클릭시
        binding.salesHistoryButton.setOnClickListener {
            startActivity(Intent(this, MyFormActivity::class.java))
        }

        // 구매 내역 버튼 클릭시
        binding.orderListButton.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

        // 작성한 글 버튼 클릭시
        binding.writePostButton.setOnClickListener {
            startActivity(Intent(this, MyPostActivity::class.java ))
        }

        // 좋아요 글 버튼 클릭시
        binding.likePostButton.setOnClickListener {
            startActivity(Intent(this, LikePostActivity::class.java))
        }

        // 찜 폼 버튼 클릭시
        binding.favoriteFormButton.setOnClickListener {
            startActivity(Intent(this, FavoriteFormActivity::class.java))
        }

        // 계정/정보 관리 버튼 클릭시
        binding.profileEditButton.setOnClickListener {
            startActivity(Intent(this, AccountInfoActivity::class.java))
        }

        binding.bottomNavigationView.selectedItemId = R.id.action_mypage

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
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }
                R.id.action_search -> true
                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val file = uriToFile(uri) ?: return
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                RetrofitClient.getInstance().create(MyPageRetrofitInterface::class.java)
                    .updateProfileImg(getToken(), body)
                    .enqueue(object : Callback<APIResponse> {
                        override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                            if (response.isSuccessful) {
                                viewModel.loadUserData(getToken())
                            }
                        }

                        override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                        }
                    })
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

    companion object {
        private const val REQUEST_CODE_IMAGE = 101
    }
}