package com.example.cosmetictogether.presentation.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmetictogether.R
import com.example.cosmetictogether.presentation.login.LoginActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 일정 시간 지연 이후 실행하기 위한 코드
        Handler(Looper.getMainLooper()).postDelayed({

            // 일정 시간이 지나면 LoginActivity로 이동
            val intent= Intent( this, LoginActivity::class.java)
            startActivity(intent)

            finish()

        }, 3000) // 시간 0.5초 이후 실행
    }
}