package com.example.bluetoothapp.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BTDeviceDomain {
    return BTDeviceDomain(
        name = name,
        address = address
    )
}