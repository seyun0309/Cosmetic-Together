package com.example.cosmetictogether.presentation.signup.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.model.SignUpRequest
import com.example.cosmetictogether.presentation.login.LoginActivity
import com.example.cosmetictogether.presentation.signup.viewmodel.SignUpViewModel

class SignUpActivity : AppCompatActivity() {
    private val viewModel: SignUpViewModel by viewModels() // SignUpViewModel을 viewModel 델리게이트로 초기화 (회원가입 로직 관리)

    // UI 요소 선언
    private lateinit var nameEditText: EditText
    private lateinit var nicknameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var signupIDMessage: TextView
    private lateinit var signupPasswordMessage: TextView
    private lateinit var signupPasswordMessage2: TextView
    private lateinit var signUpButton: Button
    private lateinit var nicknameCheckButton: Button
    private lateinit var passwordCheckButton: Button

    // 상수 메시지 선언
    private companion object {
        const val NICKNAME_AVAILABLE = "사용 가능한 닉네임입니다."
        const val NICKNAME_TAKEN = "이미 사용 중인 닉네임입니다."
        const val EMPTY_NICKNAME_MESSAGE = "닉네임을 입력하세요."
        const val VALID_PASSWORD_MESSAGE = "사용 가능한 비밀번호입니다."
        const val INVALID_PASSWORD_MESSAGE = "사용할 수 없는 비밀번호입니다."
        const val PASSWORD_MATCH_MESSAGE = "비밀번호가 일치합니다."
        const val PASSWORD_MISMATCH_MESSAGE = "비밀번호가 일치하지 않습니다."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup) // 회원가입 화면 레이아웃 설정

        // UI 요소 초기화
        nameEditText = findViewById(R.id.NameText) // 이름 입력
        nicknameEditText = findViewById(R.id.NickNameText) // 닉네임 입력
        passwordEditText = findViewById(R.id.SignUpPasswordText) // 비밀번호 입력
        confirmPasswordEditText = findViewById(R.id.SignUpPasswordText2) // 비밀번호 확인 입력
        phoneEditText = findViewById(R.id.SignupPhoneText) // 전화번호 입력
        addressEditText = findViewById(R.id.SignupAddressText2) // 주소 입력
        signupIDMessage = findViewById(R.id.SignupIDMessage) // 닉네임 확인 결과 메시지
        signupPasswordMessage = findViewById(R.id.SignupPasswordMessage) // 비밀번호 유효성 검사 결과 메시지
        signupPasswordMessage2 = findViewById(R.id.SignupPasswordMessage2) // 비밀번호 일치 여부 메시지
        signUpButton = findViewById(R.id.SignUpButton) // 회원가입 버튼
        nicknameCheckButton = findViewById(R.id.NickNameCheckBtn) // 닉네임 중복 확인 버튼
        passwordCheckButton = findViewById(R.id.PasswordCheckBtn) // 비밀번호 확인 버튼

        val verifiedEmail = intent.getStringExtra("verified_email")
        val emailEditText = findViewById<TextView>(R.id.EmailText)
        emailEditText.text = verifiedEmail

        // 닉네임 중복 확인 버튼 클릭
        nicknameCheckButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            if (nickname.isNotEmpty()) {
                viewModel.checkNickname(nickname) // 닉네임이 입력되면 ViewModel을 통해 중복 확인 요청
            } else {
                Toast.makeText(this, EMPTY_NICKNAME_MESSAGE, Toast.LENGTH_SHORT).show() // 닉네임이 비어 있으면 알림 메시지 표시
            }
        }

        // 비밀번호 유효성 검사: EditText에 포커스가 변경될 때 호출
        passwordEditText.setOnFocusChangeListener { _, _ -> validatePassword() }

        // 비밀번호 일치 여부 확인 버튼 클릭
        passwordCheckButton.setOnClickListener { checkPasswordMatch() }

        // 닉네임 중복 확인 결과를 관찰하여 UI 업데이트
        viewModel.nicknameResponse.observe(this) { response ->
            response?.let {
                // 닉네임 중복 여부에 따라 메시지 설정
                signupIDMessage.text = when (it.status) {
                    0 -> NICKNAME_TAKEN
                    else -> NICKNAME_AVAILABLE
                }
            }
        }

        // 비밀번호 유효성 검사 결과를 통해 UI 업데이트
        viewModel.passwordValidation.observe(this) { isValid ->
            signupPasswordMessage.text = if (isValid) {
                VALID_PASSWORD_MESSAGE  // 전송 성공 시 성공 메시지 표시
            } else {
                INVALID_PASSWORD_MESSAGE  // 전송 실패 시 실패 메시지 표시
            }
        }

        // 비밀번호 일치 여부 결과를 통해 UI 업데이트
        viewModel.passwordMatch.observe(this) { isMatch ->
            signupPasswordMessage2.text = if (isMatch) {
                PASSWORD_MATCH_MESSAGE // 일치 시 일치 메시지 표시
            } else {
                PASSWORD_MISMATCH_MESSAGE // 불일치 시 불일치 메시지 표시
            }
            updateSignUpButtonState() // 회원가입 버튼 활성화 상태 업데이트
        }

        // 회원가입 버튼 클릭: 로그인 화면으로 이동
        signUpButton.setOnClickListener {
            val signUpRequest = SignUpRequest(
                userName = nameEditText.text.toString(),
                email = verifiedEmail ?: "",
                password = passwordEditText.text.toString(),
                phone = phoneEditText.text.toString(),
                nickname = nicknameEditText.text.toString(),
                address = addressEditText.text.toString()
            )

            // 회원가입 요청을 보냄
            viewModel.signUp(
                signUpRequest.userName,
                signUpRequest.email,
                signUpRequest.password,
                signUpRequest.phone,
                signUpRequest.nickname,
                signUpRequest.address
            )

            // 회원가입 결과 관찰
            viewModel.signUpResponse.observe(this) { response ->
                response?.let {
                    Toast.makeText(this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } ?: run {
                    Toast.makeText(this, "회원가입 실패: 응답이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 비밀번호 유효성 검사 메소드
    private fun validatePassword() {
        val password = passwordEditText.text.toString()
        viewModel.validatePassword(password) // ViewModel에 비밀번호 유효성 요청
        updateSignUpButtonState() // 회원가입 버튼 활성화 상태 업데이트
    }

    // 비밀번호 일치 여부 확인 메소드
    private fun checkPasswordMatch() {
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        viewModel.checkPasswordMatch(password, confirmPassword) // ViewModel에 비밀번호 일치 여부 요청
        updateSignUpButtonState() // 회원가입 버튼 활성화 상태 업데이트
    }

    // 회원가입 버튼 활성화 상태 업데이트 메소드
    private fun updateSignUpButtonState() {
        // 모든 필드가 입력되고 유효성 검사를 통과한 경우에만 버튼 활성화
        val isFormValid = nicknameEditText.text.isNotEmpty() &&
                phoneEditText.text.isNotEmpty() &&
                addressEditText.text.isNotEmpty() &&
                passwordEditText.text.isNotEmpty() &&
                confirmPasswordEditText.text.isNotEmpty() &&
                signupIDMessage.text == NICKNAME_AVAILABLE &&
                signupPasswordMessage.text == VALID_PASSWORD_MESSAGE &&
                signupPasswordMessage2.text == PASSWORD_MATCH_MESSAGE

        signUpButton.isEnabled = isFormValid // 버튼 활성화 여부 설정
    }
}
