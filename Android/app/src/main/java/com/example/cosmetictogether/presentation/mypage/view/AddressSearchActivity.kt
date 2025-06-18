package com.example.cosmetictogether.presentation.mypage.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class AddressSearchActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        val settings = webView.settings
        settings.javaScriptEnabled = true

        webView.addJavascriptInterface(AddressBridge(), "AndroidInterface")
        webView.loadUrl("https://seyun0309.github.io/daum-postcode/postcode.html")
    }

    inner class AddressBridge {
        @JavascriptInterface
        fun processAddress(address: String) {
            val resultIntent = Intent()
            Log.d("ADDRESS", "주소 선택됨: $address")
            resultIntent.putExtra("selectedAddress", address)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}