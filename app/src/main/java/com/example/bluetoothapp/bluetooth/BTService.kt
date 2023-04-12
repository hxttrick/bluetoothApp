package com.example.bluetoothapp.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

private const val TAG = "BTService"

const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

class BTService(private val handler: Handler) {

    val MY_UUID = UUID.fromString("dcd8925a-379e-4c4f-8ef8-b925c0f903c7")
    val CONNECT_FAILED = 2

    @SuppressLint("MissingPermission")
    inner class ConnectThread(device: BluetoothDevice, val adapter: BluetoothAdapter) : Thread() {
        private val mSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        override fun run() {
            adapter.cancelDiscovery()

            mSocket?.let { socket ->
                try {
                    socket.connect()
                } catch (e: IOException) {
                    Log.e(TAG, e.toString())
                    handler.obtainMessage(CONNECT_FAILED).sendToTarget()
                }
            }
        }

        fun cancel() {
            try {
                mSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    inner class ConnectedThread(private val socket: BluetoothSocket) : Thread() {

        private val outStream: OutputStream = socket.outputStream
        private val buffer: ByteArray = ByteArray(1024)

        fun write(bytes: ByteArray) {
            try {
                outStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred whilst sending data", e)

                val writeErrorMessage = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "An error occurred")
                }
                writeErrorMessage.data = bundle
                handler.sendMessage(writeErrorMessage)
                return
            }

            val writtenMessage = handler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
            writtenMessage.sendToTarget()
        }

        fun cancel() {
            try {
                socket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred whilst closing socket", e)
            }
        }
    }
}