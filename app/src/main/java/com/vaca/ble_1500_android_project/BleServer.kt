package com.vaca.ble_1500_android_project

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import kotlinx.coroutines.*


object BleServer {
    val scan = BleScanManager()

    val dataScope = CoroutineScope(Dispatchers.IO)
    fun setScan(bluetoothLeScanner: BluetoothLeScanner) {
        scan.setScan(bluetoothLeScanner)
    }

    fun setScanCallBack(app: Application) {
        scan.setCallBack(object : BleScanManager.Scan {
            override fun scanReturn(
                name: String,
                bluetoothDevice: BluetoothDevice,
                rssi: Int,
                press: Boolean
            ) {


            }
        })
    }



}