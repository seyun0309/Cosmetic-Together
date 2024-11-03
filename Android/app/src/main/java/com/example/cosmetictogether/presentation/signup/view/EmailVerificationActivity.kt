package com.example.cosmetictogether.presentation.signup.view

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cosmetictogether.R
import com.example.cosmetictogether.presentation.signup.viewmodel.EmailVerificationViewModel
import com.example.cosmetictogether.presentation.login.LoginActivity

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etVerificationCode: EditText
    private lateinit var btnVerifyEmail: Button
    private lateinit var btnCheckCode: Button
    private lateinit var btnJoinNext: Button
    private lateinit var btnprevious: Button
    private lateinit var viewModel: EmailVerificationViewModel

    private var receivedConfirmationCode: String? = null
    private var isVerified = false // 인증 상태를 저장하는 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        etEmail = findViewById(R.id.loginIDEditText)
        etVerificationCode = findViewById(R.id.etVerificationCode)
        btnVerifyEmail = findViewById(R.id.btnVerifyEmail)
        btnCheckCode = findViewById(R.id.btnCheckCode)
        btnJoinNext = findViewById(R.id.btnJoinNext)
        btnprevious = findViewById(R.id.toolbar_previous)

        viewModel = ViewModelProvider(this)[EmailVerificationViewModel::class.java]

        btnCheckCode.isEnabled = false
        btnJoinNext.isEnabled = false

        // 인증번호 전송 버튼 클릭 시
        btnVerifyEmail.setOnClickListener {
            val email = etEmail.text.toString()
            if (email.isNotEmpty()) {
                sendVerificationEmail(email)

                btnVerifyEmail.isEnabled = false
                startCountDownTimer(180000) // 3분 타이머 시작

                btnCheckCode.isEnabled = true
            } else {
                Log.e("EmailVerifyAct", "이메일을 입력하세요.")
            }
        }

        // 인증 확인 버튼 클릭 시
        btnCheckCode.setOnClickListener {
            verifyCode()
        }

        btnJoinNext.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("verified", isVerified) // 인증 상태 전달
            startActivity(intent)
            finish()
        }

        btnprevious.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startCountDownTimer(timeInMillis: Long) {
        object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                btnVerifyEmail.text = "${millisUntilFinished / 1000}초 후 다시 전송"
            }

            override fun onFinish() {
                btnVerifyEmail.isEnabled = true
                btnVerifyEmail.text = "인증번호 전송"
            }
        }.start()
    }

    // 인증번호 전송
    private fun sendVerificationEmail(email: String) {
        viewModel.sendVerificationEmail(email).observe(this) { response ->
            if (response != null) {
                Log.d("EmailVerifyAct", "이메일 전송 성공, 확인 코드: $receivedConfirmationCode")
                Toast.makeText(this, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("EmailVerifyAct", "이메일 전송 실패")
                Toast.makeText(this, "인증번호 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 인증 코드 확인 로직
    private fun verifyCode() {
        val enteredCode = etVerificationCode.text.toString()

        if (enteredCode == receivedConfirmationCode) {
            Log.d("EmailVerifyAct", "인증 성공!")
            Toast.makeText(this, "인증 성공!", Toast.LENGTH_SHORT).show()
            isVerified = true // 인증 성공 시 상태 업데이트
            btnJoinNext.isEnabled = true
        } else {
            Log.e("EmailVerifyAct", "인증 번호가 일치하지 않습니다.")
            Toast.makeText(this, "인증 번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}