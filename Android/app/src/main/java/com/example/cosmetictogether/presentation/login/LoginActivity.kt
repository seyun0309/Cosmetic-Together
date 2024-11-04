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
import com.example.cosmetictogether.presentation.signup.EmailVerificationActivity
import com.example.cosmetictogether.presentation.signup.viewmodel.AuthViewModel
import com.example.cosmetictogether.data.model.LoginResponse
import com.example.cosmetictogether.presentation.home.HomeActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    private lateinit var loginIDEditText: EditText
    private lateinit var loginPasswordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginIDEditText = findViewById(R.id.loginIDEditText)
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)

        loginButton.setOnClickListener {
            val email = loginIDEditText.text.toString()
            val password = loginPasswordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        signupButton.setOnClickListener {
            // Navigate to email verification activity
            startActivity(Intent(this, EmailVerificationActivity::class.java))
        }

        viewModel.loginResponse.observe(this, Observer { response ->
            handleLoginResponse(response)
        })
    }

    private fun handleLoginResponse(response: LoginResponse?) {
        response?.let {
            if (it.accessToken.isNotEmpty()) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(this, "로그인 실패: ${it.refresh}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "로그인 실패: 응답이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
