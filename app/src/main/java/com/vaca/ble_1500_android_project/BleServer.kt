package com.vaca.ble_1500_android_project

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import com.vaca.ble_1500_android_project.ble.BleDataWorker
import kotlinx.coroutines.*


object BleServer {
    val scan = BleScanManager()
    val worker=BleDataWorker()
    var connectFlag=false

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

                if(!connectFlag){
                    connectFlag=true
                    worker.initWorker(app,bluetoothDevice)
                }


            }
        })
    }



}