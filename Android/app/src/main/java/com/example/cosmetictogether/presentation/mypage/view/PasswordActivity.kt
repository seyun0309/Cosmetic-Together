package com.example.cosmetictogether.presentation.mypage.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import com.example.cosmetictogether.databinding.ActivityUpdatePasswordBinding
import com.example.cosmetictogether.presentation.mypage.viewmodel.PasswordViewModel

class PasswordActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUpdatePasswordBinding
    private val viewModel: PasswordViewModel by viewModels()


    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return "Bearer $token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 기존 비밀번호 입력 후 새로운 비밀번호 입력창 활성화
        binding.currentPasswordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputPassword = binding.currentPasswordEditText.text.toString()
                viewModel.verifyPasswordFromServer(getToken(), inputPassword) { isValid ->
                    if (isValid) {
                        binding.newPasswordEditText.visibility = View.VISIBLE
                        binding.newPasswordEditText.requestFocus()
                    } else {
                        Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            } else {
                false
            }
        }

        // 새로운 비밀번호 입력 후 비밀번호 입력창 활성화
        binding.newPasswordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputPassword = binding.newPasswordEditText.text.toString()
                if(inputPassword.isNotEmpty()) {
                    binding.newPasswordEditText2.visibility = View.VISIBLE
                    binding.newPasswordEditText2.requestFocus()
                }
                true
            } else {
                false
            }
        }

        // 비밀번호 일치 여부 확인
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val pw1 = binding.newPasswordEditText.text.toString()
                val pw2 = binding.newPasswordEditText2.text.toString()

                if (pw1.isNotBlank() && pw2.isNotBlank()) {
                    if (pw1 == pw2) {
                        binding.passwordMismatchWarning.visibility = View.GONE
                        binding.nextButton.isEnabled = true
                        binding.nextButton.setBackgroundColor(Color.parseColor("#CEDDFE"))
                        binding.nextButton.setTextColor(Color.BLACK)
                    } else {
                        binding.passwordMismatchWarning.visibility = View.VISIBLE
                        binding.nextButton.isEnabled = false
                        binding.nextButton.setBackgroundColor(Color.parseColor("#EEEEEE"))
                        binding.nextButton.setTextColor(Color.DKGRAY)
                    }
                } else {
                    // 둘 중 하나라도 비어 있으면 버튼 비활성화
                    binding.nextButton.isEnabled = false
                    binding.nextButton.setBackgroundColor(Color.parseColor("#EEEEEE"))
                    binding.nextButton.setTextColor(Color.DKGRAY)
                    binding.passwordMismatchWarning.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.newPasswordEditText.addTextChangedListener(watcher)
        binding.newPasswordEditText2.addTextChangedListener(watcher)

        // 변경 버튼
        binding.nextButton.setOnClickListener {
            val newPassword = binding.newPasswordEditText.text.toString()

            viewModel.updatePassword(getToken(), newPassword) { isSuccess ->
                runOnUiThread {
                    if (isSuccess) {
                        Toast.makeText(this, "비밀번호가 성공적으로 변경되었습니다", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, AccountInfoActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "비밀번호 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}