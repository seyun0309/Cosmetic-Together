package com.example.cosmetictogether.presentation.mypage.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmetictogether.databinding.ActivityMypageBinding
import com.example.cosmetictogether.presentation.form.view.MyFormActivity
import com.example.cosmetictogether.presentation.login.LoginActivity
import com.example.cosmetictogether.presentation.mypage.viewmodel.MyPageViewModel
import com.example.cosmetictogether.presentation.order.view.OrderActivity

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding
    private val viewModel: MyPageViewModel by viewModels()

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return token
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

        // 판매 내역 버튼 클릭시
        binding.salesHistoryButton.setOnClickListener {
            val intent = Intent(this, MyFormActivity::class.java)
            startActivity(intent)
        }

        // 구매 내역 버튼 클릭시
        binding.orderListButton.setOnClickListener {
            val intent = Intent(this, OrderActivity::class.java)
            startActivity(intent)
        }
    }
}