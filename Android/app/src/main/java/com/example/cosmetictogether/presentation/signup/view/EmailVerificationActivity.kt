package com.example.cosmetictogether.presentation.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cosmetictogether.R
import com.example.cosmetictogether.presentation.signup.viewmodel.AuthViewModel
import com.example.cosmetictogether.data.model.SendEmailResponse
import com.example.cosmetictogether.data.model.SendVerifiedResponse
import com.example.cosmetictogether.presentation.signup.view.SignUpActivity

class EmailVerificationActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    private lateinit var loginIDEditText: EditText
    private lateinit var etVerificationCode: EditText
    private lateinit var btnVerifyEmail: Button
    private lateinit var btnCheckCode: Button
    private lateinit var btnJoinNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        loginIDEditText = findViewById(R.id.loginIDEditText)
        etVerificationCode = findViewById(R.id.etVerificationCode)
        btnVerifyEmail = findViewById(R.id.btnVerifyEmail)
        btnCheckCode = findViewById(R.id.btnCheckCode)
        btnJoinNext = findViewById(R.id.btnJoinNext)

        // Send verification code when button is clicked
        btnVerifyEmail.setOnClickListener {
            val email = loginIDEditText.text.toString()
            if (email.isNotEmpty()) {
                viewModel.sendVerificationCode(email)
            } else {
                Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // Check verification code when button is clicked
        btnCheckCode.setOnClickListener {
            val email = loginIDEditText.text.toString()
            val authCode = etVerificationCode.text.toString()
            if (email.isNotEmpty() && authCode.isNotEmpty()) {
                viewModel.checkVerificationCode(email, authCode)
            } else {
                Toast.makeText(this, "이메일과 인증번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.sendEmailResponse.observe(this, Observer { response -> // LiveData 관찰 / 값이 변경될 때마다 정의된 콜백 호출
            response?.let { // response가 null이 아닐 경우에만 블록 내 코드 실행 / let 블록 내에서는 response를 it으로 참조 가능
                Toast.makeText(this, it.confirmation ?: "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show()
            } ?: run { // response가 null인 경우
                Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.sendVerifiedResponse.observe(this, Observer { response ->
            val btnJoinNext = findViewById<Button>(R.id.btnJoinNext) // 버튼 참조
            if (response != null) {
                // Access the properties directly from the SendVerifiedResponse
                if (response.success) { // 서버에서 반환된 success 속성을 확인
                    Toast.makeText(this, "인증 성공: ${response.responseMessage}", Toast.LENGTH_SHORT).show()
                    btnJoinNext.isEnabled = true // 인증 성공 시 버튼 활성화
                } else {
                    Toast.makeText(this, "인증 실패: ${response.responseMessage}", Toast.LENGTH_SHORT).show() // 실패 메시지 업데이트
                    btnJoinNext.isEnabled = false
                }
            } else {
                Toast.makeText(this, "인증 실패: 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                btnJoinNext.isEnabled = false
            }
        })

        btnJoinNext.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
