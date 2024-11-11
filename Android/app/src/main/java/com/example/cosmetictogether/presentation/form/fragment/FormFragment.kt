package com.example.cosmetictogether.presentation.form.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cosmetictogether.R

class FormFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_form_write.xml 레이아웃을 인플레이트합니다.
        return inflater.inflate(R.layout.fragment_form_write, container, false)
    }
}