package com.example.bluetoothapp.bluetooth

typealias BTDeviceDomain = BTDevice

data class BTDevice(
    val name: String?,
    val address: String
)