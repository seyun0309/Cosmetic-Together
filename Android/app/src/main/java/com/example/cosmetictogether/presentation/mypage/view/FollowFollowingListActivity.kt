package com.example.cosmetictogether.presentation.mypage.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.APIResponse
import com.example.cosmetictogether.databinding.ActivityFollowFollowingListBinding
import com.example.cosmetictogether.presentation.mypage.adapter.FollowerAdapter
import com.example.cosmetictogether.presentation.mypage.adapter.FollowingAdapter
import com.example.cosmetictogether.presentation.mypage.viewmodel.FollowFollowingListViewModel
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowFollowingListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFollowFollowingListBinding
    private val viewModel : FollowFollowingListViewModel by viewModels()

    private lateinit var followerAdapter: FollowerAdapter
    private lateinit var followingAdapter: FollowingAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFollowFollowingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel;
        binding.lifecycleOwner = this

        // 어댑터 초기화
        followerAdapter = FollowerAdapter()
        followingAdapter = FollowingAdapter { item ->
            val memberId = item.followingMemberId

            RetrofitClient.mypageApi.followOrUnfollowByMemberId(getToken(), memberId)
                .enqueue(object : Callback<APIResponse> {
                    override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                        if (response.isSuccessful) {
                            if (response.isSuccessful) {
                                val updatedList = viewModel.followingList.value?.filterNot {
                                    it.followingMemberId == item.followingMemberId
                                } ?: emptyList()
                                viewModel.setFollowingList(updatedList)
                            }

                            val updatedList = viewModel.followingList.value?.map {
                                if (it.followingMemberId == item.followingMemberId) {
                                    it.copy(following = !it.following)
                                } else it
                            } ?: emptyList()
                            viewModel.setFollowingList(updatedList)
                        }
                    }

                    override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                        Log.e("Follow", "Follow/unfollow 실패", t)
                    }
                })
        }

        // 기본 어댑터 설정
        binding.memberRecyclerView.adapter = followerAdapter

        // 데이터 옵저버 연결
        viewModel.followerList.observe(this) {
            followerAdapter.submitList(it)
        }
        viewModel.followingList.observe(this) {
            followingAdapter.submitList(it)
        }

        // 탭에 따라 어댑터와 데이터 전환
        val selectedTab = intent.getStringExtra("tab")
        val tabLayout = binding.postTabLayout

        if (selectedTab == "following") {
            tabLayout.selectTab(tabLayout.getTabAt(1))
            binding.memberRecyclerView.adapter = followingAdapter
            viewModel.loadFollowingList(getToken())
        } else {
            tabLayout.selectTab(tabLayout.getTabAt(0))
            binding.memberRecyclerView.adapter = followerAdapter
            viewModel.loadFollowerList(getToken())
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        binding.memberRecyclerView.adapter = followerAdapter
                        viewModel.loadFollowerList(getToken())
                    }

                    1 -> {
                        binding.memberRecyclerView.adapter = followingAdapter
                        viewModel.loadFollowingList(getToken())
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // 뒤로가기
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun getToken(): String {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null) ?: ""
        val authToken = "Bearer $token"
        return authToken
    }
}