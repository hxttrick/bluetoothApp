package com.example.bluetoothapp.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothapp.depricated.BTListAdapter
import com.example.bluetoothapp.R
import com.example.bluetoothapp.bluetooth.BTController
import com.example.bluetoothapp.bluetooth.BTService

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {

    //global variables
    private var itemTitleList = ArrayList<String>()
    private var itemAddressList = ArrayList<String>()
    private var deviceList = ArrayList<BluetoothDevice>()
    private var deviceListAdapter = BTListAdapter(this, itemTitleList, itemAddressList, deviceList)
    //private val mHandler = Handler(mainLooper)


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_list)

        //permissions
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
        }
        permissionHandler(permissions)

        //enable bluetooth if disabled
        val btManager = applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val btAdapter: BluetoothAdapter = btManager.adapter

        if (!btAdapter.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, 1)
        }

        //register device list layout and adapter
        val deviceList = findViewById<RecyclerView>(R.id.rvDeviceList)
        deviceList.layoutManager = LinearLayoutManager(this)
        deviceList.adapter = deviceListAdapter

        //bond with device in list
        connectDevice(btAdapter)

        //find paired devices and add to device list
        findPairedDevices(btAdapter)

        //register receiver for device scanning, method adds found devices to device list
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
        btAdapter.startDiscovery()

    }

    private fun connectDevice(btAdapter: BluetoothAdapter) {
        deviceListAdapter.setOnItemClickListener(object : BTListAdapter.onItemClickListener{
            override fun onItemClick(pos: Int) {
                BTService(mHandler).ConnectThread(deviceList[pos], btAdapter).start()
            }
        })

    }


    private fun permissionHandler(permissions: Array<String>) {
        permissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, arrayOf(permission), permissions.indexOf(permission) + 1)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun findPairedDevices(btAdapter: BluetoothAdapter){
        val pairedDevices: Set<BluetoothDevice>? = btAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceMacAddress = device.address
            addDeviceToList(deviceName, deviceMacAddress, device)
        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                        } else {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }

                    val deviceName = if (device?.name == null) "Unknown device" else device.name

                    val deviceMacAddress = if (device?.address == null) "00-00-00-00-00-00" else device.address

                    if (deviceName != null && deviceMacAddress != null && device != null) {
                        addDeviceToList(deviceName, deviceMacAddress, device)
                    }
                }
            }
        }
    }

    /*private val receiver2 = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                        } else {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }
                    Toast.makeText(this@MainActivity, "Bond state changed for ${device?.name} to ${device?.bondState}", Toast.LENGTH_SHORT).show()
                    when(device?.bondState) {
                        BluetoothDevice.BOND_BONDED -> {Log.d("MainActivity", "${device.name} bonded!")}
                        BluetoothDevice.BOND_BONDING -> {Log.d("MainActivity", "Attempting bond with ${device.name}...")}
                        BluetoothDevice.BOND_NONE -> {Log.d("MainActivity", "Bond state for ${device.name}: BOND_NONE")}
                    }
                }
            }
        }

    }*/

    @SuppressLint("NotifyDataSetChanged")
    fun addDeviceToList(itemTitle: String, itemAddress: String, itemDevice: BluetoothDevice) {
        itemTitleList.add(itemTitle)
        itemAddressList.add(itemAddress)
        deviceList.add(itemDevice)

        deviceListAdapter.notifyDataSetChanged()
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        private val OFF_CODE = 0
        private val ON_CODE = 1
        private val CONNECT_FAILED = 3

        override fun handleMessage(msg: Message) {

            when(msg.what) {
                OFF_CODE -> {}
                ON_CODE -> {}
                CONNECT_FAILED -> {Toast.makeText(this@MainActivity, "Connection failed", Toast.LENGTH_SHORT).show()}
                else -> { /*error*/ }
            }

            super.handleMessage(msg)
        }
    }


    fun addScannedDeviceToList(btAdapter: BluetoothAdapter) {
        BTController(this).scannedDevices

    }

    private fun writeDataToArduino(device: BluetoothDevice, adapter: BluetoothAdapter) {
        BTService(mHandler).ConnectThread(device, adapter).start()

    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)

        val btManager = applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val btAdapter: BluetoothAdapter = btManager.adapter
        btAdapter.cancelDiscovery()
    }
}