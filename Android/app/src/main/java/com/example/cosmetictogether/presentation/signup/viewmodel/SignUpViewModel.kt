package com.example.cosmetictogether.presentation.signup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.NicknameCheckRequest
import com.example.cosmetictogether.data.model.NicknameCheckResponse
import com.example.cosmetictogether.data.model.SignUpRequest
import com.example.cosmetictogether.data.model.SignUpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {
    // 닉네임 중복 확인 응답 데이터 저장 LiveData
    private val _nicknameResponse = MutableLiveData<NicknameCheckResponse?>()
    val nicknameResponse: LiveData<NicknameCheckResponse?> get() = _nicknameResponse

    // 비밀번호 유효성 검사 결과 저장 LiveData
    private val _passwordValidation = MutableLiveData<Boolean>()
    val passwordValidation: LiveData<Boolean> get() = _passwordValidation

    // 비밀번호 일치 여부 저장 LiveData
    private val _passwordMatch = MutableLiveData<Boolean>()
    val passwordMatch: LiveData<Boolean> get() = _passwordMatch

    // 회원가입 응답 데이터 저장 LiveData
    private val _signUpResponse = MutableLiveData<SignUpResponse?>()
    val signUpResponse: LiveData<SignUpResponse?> get() = _signUpResponse

    // 닉네임 중복 확인 메소드
    fun checkNickname(nickname: String) {
        Log.d("SignUpViewModel", "Checking nickname: $nickname") // 로그 출력
        val request = NicknameCheckRequest(nickname) // 닉네임 중복 확인 요청 객체 생성
        RetrofitClient.apiService.nicknameCheck(request).enqueue(object : Callback<NicknameCheckResponse> {
            override fun onResponse(call: Call<NicknameCheckResponse>, response: Response<NicknameCheckResponse>) {
                if (response.isSuccessful) { // 응답이 성공적일 때
                    val responseBody = response.body()
                    _nicknameResponse.value = responseBody // 응답 데이터를 _nicknameResponse에 저장
                    // 응답 상태가 200인지 확인하여 적절한 메시지를 설정
                    if (responseBody != null && responseBody.status == 200) {
                        _nicknameResponse.value = responseBody
                    } else {
                        // 서버에서 200 이외의 상태가 반환된 경우
                        _nicknameResponse.value = NicknameCheckResponse(1, "이미 사용 중인 닉네임입니다.") // 예시 상태값 설정
                    }
                } else {
                    Log.e("SignUpViewModel", "닉네임 중복 검사 실패: ${response.errorBody()?.string()}")
                    _nicknameResponse.value = null // 이전 응답을 초기화
                }
            }

            override fun onFailure(call: Call<NicknameCheckResponse>, t: Throwable) {
                Log.e("SignUpViewModel", "닉네임 중복 검사 실패: ${t.message}")
                _nicknameResponse.value = null // 이전 응답을 초기화
            }
        })
    }

    // 비밀번호 유효성 검사 메소드
    fun validatePassword(password: String) {
        _passwordValidation.value = password.length in 8..16 && // 8~16자 사이의 길이
                password.any { it.isDigit() } &&         // 숫자가 하나 이상
                password.any { it.isLetter() } &&        // 문자가 하나 이상
                password.any { !it.isLetterOrDigit() }   // 특수 문자가 하나 이상
    }

    // 비밀번호와 비밀번호 확인 일치 확인하는 메소드
    fun checkPasswordMatch(password: String, confirmPassword: String) {
        _passwordMatch.value = password == confirmPassword // 비밀번호와 확인이 일치하면 true, 그렇지 않으면 false
    }

    // 회원가입 요청 메소드
    fun signUp(userName: String, email: String, password: String, phone: String, nickname: String, address: String) {
        val request = SignUpRequest(userName, email, password, phone, nickname, address)
        RetrofitClient.apiService.signUp(request).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    _signUpResponse.value = response.body() // 성공 응답을 LiveData에 저장
                } else {
                    Log.e("SignUpViewModel", "회원가입 실패")
                    _signUpResponse.value = null // 실패 시 LiveData 초기화
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Log.e("SignUpViewModel", "회원가입 요청 실패: ${t.message}")
                _signUpResponse.value = null
            }
        })
    }
}
