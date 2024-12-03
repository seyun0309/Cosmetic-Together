package com.example.cosmetictogether.presentation.form.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cosmetictogether.data.api.FormRetrofitInterface
import com.example.cosmetictogether.data.api.RetrofitClient
import com.example.cosmetictogether.data.model.MyFormResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFormViewModel : ViewModel(){

    private val _myForms = MutableLiveData<List<MyFormResponse>>()
    val myForms: LiveData<List<MyFormResponse>> get() = _myForms

    private val orderApi: FormRetrofitInterface =
        RetrofitClient.getInstance().create(FormRetrofitInterface::class.java)

    fun loadFormList(token: String) {
        orderApi.getMyForm(token).enqueue(object : Callback<List<MyFormResponse>> {
            override fun onResponse(call: Call<List<MyFormResponse>>, response: Response<List<MyFormResponse>>) {
                if(response.isSuccessful) {
                    response.body()?.let { data ->
                        if(data.isNotEmpty()) {
                            _myForms.value = response.body()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<MyFormResponse>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}