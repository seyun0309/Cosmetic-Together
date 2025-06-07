package com.example.cosmetictogether.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cosmetictogether.R
import com.example.cosmetictogether.presentation.signup.view.EmailVerificationActivity
import com.example.cosmetictogether.presentation.signup.viewmodel.AuthViewModel
import com.example.cosmetictogether.data.model.LoginResponse
import com.example.cosmetictogether.presentation.post.view.PostActivity

class LoginActivity : AppCompatActivity() {
    // AuthViewModel을 viewModels 델리게이트로 초기화 (ViewModel로 데이터 관리)
    private val viewModel: AuthViewModel by viewModels()

    // UI 요소 선언
    private lateinit var loginIDEditText: EditText
    private lateinit var loginPasswordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // UI 요소 초기화
        loginIDEditText = findViewById(R.id.loginIDEditText) // 아이디 입력
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText) // 비밀번호 입력
        loginButton = findViewById(R.id.loginButton) // 로그인 버튼
        signupButton = findViewById(R.id.signupButton) // 회원가입 버튼

        // 로그인 버튼 클릭
        loginButton.setOnClickListener {
            val email = loginIDEditText.text.toString() // 입력한 이메일 가져오기
            val password = loginPasswordEditText.text.toString() // 입력한 비밀번호 가져오기
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password, this) // 이메일과 비밀번호가 비어있지 않으면 ViewModel을 통해 로그인 요청
            } else {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show() // 입력이 비어 있으면 알림 메시지 표시
            }
        }

        // 회원가입 버튼 클릭
        signupButton.setOnClickListener {
            startActivity(Intent(this, EmailVerificationActivity::class.java))
        }

        viewModel.loginResponse.observe(this, Observer { response ->
            handleLoginResponse(response)
        }) // ViewModel의 loginResponse를 관찰하여 로그인 응답 처리
    }

    // 로그인 응답 처리 함수
    private fun handleLoginResponse(response: LoginResponse?) {
        response?.let {
            if (it.accessToken.isNotEmpty()) {
                startActivity(Intent(this, PostActivity::class.java)) // 로그인 성공 시 HomeActivity로 이동
            } else {
                Toast.makeText(this, "로그인 실패: ${it.refreshToken}", Toast.LENGTH_SHORT).show()
            } // 로그인 실패 시 실패 메시지 표시 (refreshToken 사용)
        } ?: run {
            Toast.makeText(this, "로그인 실패: 응답이 없습니다.", Toast.LENGTH_SHORT).show()
        } // 응답이 없을 경우 실패 메시지 표시
    }
}
