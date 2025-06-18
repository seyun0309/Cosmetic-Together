package com.example.cosmetictogether.presentation.mypage.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmetictogether.databinding.ActivityAccountInfoBinding
import com.example.cosmetictogether.presentation.mypage.viewmodel.AccountInfoViewModel

class AccountInfoActivity : AppCompatActivity() {

    private val NICKNAME_REQUEST_CODE = 1002

    private lateinit var binding: ActivityAccountInfoBinding
    private val viewModel: AccountInfoViewModel by viewModels()

    companion object {
        private const val ADDRESS_REQUEST_CODE = 1001
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        return "Bearer $token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.loadAccountInfo(getToken())

        // 뒤로가기
        binding.backBtn.setOnClickListener {
            finish()
        }

        // 닉네임 변경 버튼
        binding.verifyAction.setOnClickListener {
            startActivityForResult(Intent(this, NicknameActivity::class.java), NICKNAME_REQUEST_CODE)
        }

        // 비밀번호 변경 버튼
        binding.passwordValue.setOnClickListener {
            startActivity(Intent(this, PasswordActivity::class.java))
        }

        // 주소 변경 버튼
        binding.addressLabel.setOnClickListener {
            startActivityForResult(Intent(this, AddressSearchActivity::class.java), ADDRESS_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADDRESS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedAddress = data?.getStringExtra("selectedAddress")
            selectedAddress?.let {
                binding.addressValue.text = it
                viewModel.updateAddress(getToken(), it)
            }
        }
    }
}