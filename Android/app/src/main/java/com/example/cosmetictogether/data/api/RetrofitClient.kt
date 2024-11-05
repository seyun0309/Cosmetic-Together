package com.example.cosmetictogether.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient { // Retrofit 설정 담당 객체
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit: Retrofit by lazy { // Retrofit 인스턴스를 lazy를 통해 초기화(필요 시 한 번만 생성)
        // 로깅 인터셉터 추가 (네트워크 요청과 응답을 로그로 확인할 수 있도록 설정)
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        } // BODY 레벨로 설정하여 요청 및 응답의 모든 내용을 로그로 출력

        val client = OkHttpClient.Builder() // OkHttpClient 생성 (HTTP 요청을 담당하는 클라이언트로, 로깅 인터셉터 추가)
            .addInterceptor(logging) // 클라이언트에 로깅 인터셉터 추가
            .build()

        Retrofit.Builder() // Retrofit 빌더를 사용해 Retrofit 객체 생성
            .baseUrl(BASE_URL) // 기본 URL 설정
            .client(client) // OkHttpClient 설정
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기 추가
            .build()
    }

    val apiService: AuthRetrofitInterface by lazy { // API 인터페이스 구현체를 제공하는 프로퍼티
        retrofit.create(AuthRetrofitInterface::class.java) // API 인터페이스 생성
    }

    fun getInstance(): Retrofit { // 회원가입 구현 시 추가(외부에서 Retrofit 인스턴스에 접근할 수 있도록 getInstance 함수 제공)
        return retrofit // 생성된 Retrofit 인스턴스 반환
    }
}
