package com.rexus.bandselector.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rexus.bandselector.data.TelephonyRepo
import com.rexus.bandselector.data.model.SignalMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = TelephonyRepo(application)

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage

    val metrics: StateFlow<SignalMetrics> = repo.observeSignalMetrics()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SignalMetrics()
        )
}
