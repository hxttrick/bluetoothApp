package com.example.bluetoothapp.presentation

import com.example.bluetoothapp.bluetooth.BTDevice

data class BTUiState(
    val scannedDevices: List<BTDevice> = emptyList(),
    val pairedDevices: List<BTDevice> = emptyList(),
)
