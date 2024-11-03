package com.example.cosmetictogether.presentation.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmetictogether.R
import androidx.databinding.DataBindingUtil
import com.example.cosmetictogether.data.api.AuthService
import com.example.cosmetictogether.data.api.LoginView
import com.example.cosmetictogether.databinding.ActivityLoginBinding
import com.example.cosmetictogether.presentation.home.HomeActivity
import com.example.cosmetictogether.presentation.signup.view.EmailVerificationActivity

class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var binding: ActivityLoginBinding
    private val authService = AuthService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        authService.setLoginView(this)

        binding.loginButton.setOnClickListener {
            val email = binding.loginIDEditText.text.toString().trim()
            val password = binding.loginPasswordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                authService.login(email, password)  // Query 파라미터로 전달
            }
        }

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, EmailVerificationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.loginIDEditText.error = "이메일을 입력해주세요."
                false
            }
            password.isEmpty() -> {
                binding.loginPasswordEditText.error = "비밀번호를 입력해주세요."
                false
            }
            else -> true
        }
    }

    override fun onSignInSuccess(token: String) {
        Log.d("LoginActivity", "로그인 성공: 사용자 token = $token")
        saveToken(token) // JWT 토큰을 저장합니다.
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("X-AUTH-TOKEN", token) // 토큰을 X-AUTH-TOKEN으로 저장
        editor.apply()
    }

    override fun onSignInFailure() {
        Log.d("SignInActivity", "로그인 실패")
        Toast.makeText(this, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
    }
}
