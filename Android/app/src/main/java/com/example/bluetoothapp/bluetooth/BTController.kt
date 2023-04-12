package com.example.bluetoothapp.bluetooth

import android.annotation.SuppressLint
import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.example.bluetoothapp.presentation.MainActivity
import kotlinx.coroutines.flow.*

@SuppressLint("MissingPermission")
class BTController(private val context: Context) : IBTController {

    private val btManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val btAdapter by lazy {
        btManager?.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BTDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BTDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BTDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BTDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if(newDevice in devices) devices else devices + newDevice
    }}

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        context.registerReceiver(foundDeviceReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        btAdapter?.startDiscovery()
    }


    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }

        btAdapter?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
    }

    private fun updatePairedDevices() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }

        btAdapter?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices }
            }
    }

    private fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}