package com.example.jobsearchapp.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {
    private val _isPurpleTheme = MutableStateFlow(true)
    val isPurpleTheme = _isPurpleTheme.asStateFlow()

    fun toggleTheme() {
        _isPurpleTheme.value = !_isPurpleTheme.value
    }
}