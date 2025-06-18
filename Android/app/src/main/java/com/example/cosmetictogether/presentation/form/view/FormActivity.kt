package com.example.cosmetictogether.presentation.form.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cosmetictogether.R
import com.example.cosmetictogether.data.model.AccountResponse
import com.example.cosmetictogether.databinding.ActivityFormBinding
import com.example.cosmetictogether.presentation.form.adapter.FormAdapter
import com.example.cosmetictogether.presentation.form.viewmodel.FormViewModel
import com.example.cosmetictogether.presentation.mypage.view.MyPageActivity
import com.example.cosmetictogether.presentation.post.view.PostActivity
import com.example.cosmetictogether.presentation.search.view.SearchActivity
import com.google.android.material.tabs.TabLayout

class FormActivity : AppCompatActivity() {
    private lateinit var formAdapter: FormAdapter
    private lateinit var binding: ActivityFormBinding
    private val viewModel: FormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // ListAdapter 사용하여 어댑터 초기화
        formAdapter = FormAdapter()
        binding.formRecyclerView.adapter = formAdapter

        viewModel.loadFormSummaryData()

        // SHOW_DIALOG 플래그 확인
        val shouldShowDialog = intent.getBooleanExtra("SHOW_DIALOG", false)
        if (shouldShowDialog) {
            val orderId = intent.getLongExtra("orderId", -1L)
            if(orderId != -1L) {
                viewModel.showAccount(orderId)

                // API 응답 데이터 관찰
                viewModel.accountData.observe(this) { accountData ->
                    accountData?.let {
                        showFormInfoDialog(it) // 다이얼로그 표시
                    }
                }
            }
        }

        // 탭 클릭 리스너 설정
        binding.postTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val token = getToken()

                when (tab?.position) {
                    0 -> viewModel.loadFormSummaryData()              // 최근 탭
                    1 -> viewModel.loadFollowingForm(token)       // 팔로잉 탭
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 폼 작성 버튼 클릭
        binding.createFormBtn.setOnClickListener {
            startActivity(Intent(this, FormCreateActivity::class.java))
        }

        // 데이터 업데이트 시 RecyclerView에 반영
        viewModel.formData.observe(this, Observer { formList ->
            formAdapter.submitList(formList) // ListAdapter에서는 submitList 사용 가능
        })

        //폼 세부 조회
        formAdapter.setOnItemClickListener { formId ->
            // 선택된 formId를 Intent로 전달하여 FormDetailActivity로 이동
            val intent = Intent(this, FormDetailActivity::class.java)
            intent.putExtra("formId", formId)
            startActivity(intent)
        }

        binding.bottomNavigationView.selectedItemId = R.id.action_form

        // BottomNavigationView의 항목 클릭 이벤트 설정
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }
                R.id.action_form -> {
                    // 현재 FormActivity이므로 이동하지 않음
                    true
                }
                R.id.action_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                R.id.action_mypage -> {
                    // MyPageActivity로 이동
                    startActivity(Intent(this, MyPageActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showFormInfoDialog(accountResponse: AccountResponse) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_account_info)

        val organizerName: TextView = dialog.findViewById(R.id.tvOrganizerName)
        val tvFormTitle: TextView = dialog.findViewById(R.id.tvFormTitle)
        val tvFormDescription: TextView = dialog.findViewById(R.id.tvFormDescription)
        val btnClose: Button = dialog.findViewById(R.id.btnClose)

        // 데이터 바인딩
        organizerName.text = "예금주 : " + accountResponse.organizerName
        tvFormTitle.text = "은행명 : " + accountResponse.bankName
        tvFormDescription.text = "계좌번호 : " + accountResponse.accountNumber

        // 닫기 버튼 동작
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        val authToken = "Bearer $token"
        return authToken
    }
}
