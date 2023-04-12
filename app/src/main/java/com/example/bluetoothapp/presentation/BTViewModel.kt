package com.example.bluetoothapp.presentation
/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothapp.bluetooth.BTController
import com.example.bluetoothapp.presentation.BTUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BTViewModel @Inject constructor(
    private val btController: BTController
) : ViewModel() {

    private val _state = MutableStateFlow(BTUiState())
    val state = combine(
        btController.scannedDevices,
        btController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun startScan() {
        btController.startDiscovery()
    }

    fun stopScan() {
        btController.stopDiscovery()
    }
}
 */
class BTViewModel{}