package com.example.bluetoothapp.bluetooth

import kotlinx.coroutines.flow.StateFlow

interface IBTController {
    val scannedDevices: StateFlow<List<BTDevice>>
    val pairedDevices: StateFlow<List<BTDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    fun release()
}