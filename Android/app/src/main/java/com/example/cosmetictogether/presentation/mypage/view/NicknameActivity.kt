package com.example.cosmetictogether.presentation.mypage.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmetictogether.databinding.ActivityUpdateNicknameBinding
import com.example.cosmetictogether.presentation.mypage.viewmodel.NicknameViewModel

class NicknameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateNicknameBinding
    private val viewModel: NicknameViewModel by viewModels()

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return "Bearer $token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 중복 검사 버튼
        binding.duplicateButton.setOnClickListener {
            val nickname = binding.currentPasswordEditText.text.toString()

            if (nickname.isBlank()) {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.checkNicknameDuplicate(nickname) { isAvailable, message ->
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                    if (isAvailable) {
                        // 닉네임 사용 가능 시 nextButton 활성화
                        binding.nextButton.isEnabled = true
                        binding.nextButton.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))
                        binding.nextButton.setTextColor(resources.getColor(android.R.color.white))
                    } else {
                        binding.nextButton.isEnabled = false
                        binding.nextButton.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
                        binding.nextButton.setTextColor(resources.getColor(android.R.color.black))
                    }
                }
            }
        }

        // 변경 버튼
        binding.nextButton.setOnClickListener {
            val nickname = binding.currentPasswordEditText.text.toString()
            viewModel.updateNickname(getToken(), nickname) { isSuccess ->
                runOnUiThread {
                    if(isSuccess) {
                        val resultIntent = Intent().apply {
                            putExtra("updatedNickname", nickname)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                        Toast.makeText(this, "닉네임이 변경되었습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "닉네임 변경에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }
}