package com.example.cointrace.ui.compte

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CompteViewModel : ViewModel() {

    var isLoggedIn: Boolean = false
    var currentUsername: String? = null
    private val _text = MutableLiveData<String>().apply {
        value = "Login"
    }
    val text: LiveData<String> = _text
}