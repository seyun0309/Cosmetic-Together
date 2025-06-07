package com.example.cosmetictogether.presentation.signup.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cosmetictogether.R
import com.example.cosmetictogether.presentation.login.LoginActivity
import com.example.cosmetictogether.presentation.signup.viewmodel.AuthViewModel

class EmailVerificationActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels() // AuthViewModel을 viewModel 델리게이트로 초기화 (인증 로직 관리)

    // UI 요소 선언
    private lateinit var loginIDEditText: EditText
    private lateinit var etVerificationCode: EditText
    private lateinit var btnVerifyEmail: Button
    private lateinit var btnCheckCode: Button
    private lateinit var btnJoinNext: Button
    private lateinit var backBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email) // 이메일 인증 화면 레이아웃 설정

        // UI 요소 초기화
        loginIDEditText = findViewById(R.id.loginIDEditText) // 이메일 입력
        etVerificationCode = findViewById(R.id.etVerificationCode) // 인증 번호 입력
        btnVerifyEmail = findViewById(R.id.btnVerifyEmail) // 인증 번호 요청 버튼
        btnCheckCode = findViewById(R.id.btnCheckCode) // 인증 번호 확인 버튼
        btnJoinNext = findViewById(R.id.btnJoinNext) // 다음 화면 이동 버튼 (회원가입)
        backBtn = findViewById(R.id.backBtn) // 뒤로가기 버튼

        // 뒤로가기 버튼 클릭
        backBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 이메일 인증 번호 요청 버튼 클릭
        btnVerifyEmail.setOnClickListener {
            val email = loginIDEditText.text.toString()
            if (email.isNotEmpty()) {
                viewModel.sendVerificationCode(email) // 이메일이 입력되면 ViewModel을 통해 인증 코드 전송 요청
            } else {
                Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show() // 이메일이 비어 있으면 알림 메시지 표시
            }
        }

        // 인증 번호 확인 버튼 클릭
        btnCheckCode.setOnClickListener {
            val email = loginIDEditText.text.toString() // 입력한 이메일 가져오기
            val authCode = etVerificationCode.text.toString() // 입력한 인증 번호 가져오기
            if (email.isNotEmpty() && authCode.isNotEmpty()) {
                viewModel.checkVerificationCode(email, authCode) // 이메일과 인증 번호 입력된 경우 인증 번호 확인 요청
            } else {
                Toast.makeText(this, "이메일과 인증번호를 입력하세요.", Toast.LENGTH_SHORT).show() // 입력이 비어 있으면 알림 메시지 표시
            }
        }

        // 인증 코드 전송 결과를 통해 UI 업데이트
        viewModel.sendEmailResponse.observe(this, Observer { response ->
            response?.let {
                Toast.makeText(this, it.confirmation ?: "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show() // 전송 성공 시 성공 메시지 표시
            } ?: run {
                Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show() // 전송 실패 시 오류 메시지 표시
            }
        })

        // 인증 코드 확인 결과를 통해 UI 업데이트
        viewModel.sendVerifiedResponse.observe(this, Observer { response ->
            val btnJoinNext = findViewById<Button>(R.id.btnJoinNext) // 회원가입 버튼 참조
            if (response != null) {
                if (response.success) {
                    Toast.makeText(this, "인증 성공: ${response.responseMessage}", Toast.LENGTH_SHORT).show() // 인증 성공 시 성공 메시지 표시 및 다음 버튼 활성화
                    btnJoinNext.isEnabled = true
                } else {
                    Toast.makeText(this, "인증 실패: ${response.responseMessage}", Toast.LENGTH_SHORT).show() // 인증 실패 시 실패 메시지 표시 및 다음 버튼 비활성화
                    btnJoinNext.isEnabled = false
                }
            } else {
                // 응답이 없을 경우 실패 메시지 표시 및 다음 버튼 비활성화
                Toast.makeText(this, "인증 실패: 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                btnJoinNext.isEnabled = false
            }
        })

        // 회원가입 화면으로 이동하는 버튼 클릭
        btnJoinNext.setOnClickListener {
            val verifiedEmail = loginIDEditText.text.toString()
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("verified_email", verifiedEmail) // Pass verified email
            startActivity(intent)
        }
    }
}
