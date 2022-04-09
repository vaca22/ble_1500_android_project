package com.vaca.ble_1500_android_project

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*


class BleScanManager {
    interface Scan {
        fun scanReturn(
            name: String,
            bluetoothDevice: BluetoothDevice,
            rssi: Int,
            press: Boolean = false
        )
    }

    companion object {
        var scanState = false
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var leScanner: BluetoothLeScanner? = null
    private var scan: Scan? = null


    private fun parseRecord(scanRecord: ByteArray): Map<Int, String> {
        val ret: MutableMap<Int, String> = HashMap()
        var index = 0
        while (index < scanRecord.size) {
            val length = scanRecord[index++].toInt()
            if (length == 0) break
            val type = scanRecord[index].toInt()
            if (type == 0) break
            val data = Arrays.copyOfRange(scanRecord, index + 1, index + length)
            if (data.isNotEmpty()) {
                val hex = StringBuilder(data.size * 2)

                for (bb in data.indices) {
                    hex.append(String.format("%02X", data[bb]))
                }
                ret[type] = hex.toString()
            }
            index += length
        }
        return ret
    }

    private fun isRightScanRecord(bytes: ByteArray): Boolean {
        return "4EF301" == parseRecord(bytes)[-1]
    }


    private var leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(
            callbackType: Int,
            result: ScanResult,
        ) {
            super.onScanResult(callbackType, result)

            val device = result.device
           if (ActivityCompat.checkSelfPermission(
                    MainApplication.application,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
               val name= device.name
               val rssi = result.rssi
               if (name == null) {
                   return
               }
               if (name.isEmpty()) {
                   return
               }
               Log.e("ble scan", name)
               if (name.contains("esp32")) {
                   scan?.scanReturn(name, device, rssi)
               }
                return
            }

        }

        override fun onBatchScanResults(results: List<ScanResult>) {}
        override fun onScanFailed(errorCode: Int) {}
    }

    fun setCallBack(scan: Scan) {
        this.scan = scan
    }

    val settings: ScanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .build()


    fun setScan(bluetoothLeScanner: BluetoothLeScanner) {
        leScanner = bluetoothLeScanner
    }

    fun start() {
        val bluetoothManager =
            MainApplication.application.getSystemService(AppCompatActivity.BLUETOOTH_SERVICE) as BluetoothManager
        MainActivity.mBluetoothAdapter = bluetoothManager.adapter
        val scanner = MainActivity.mBluetoothAdapter.bluetoothLeScanner
        if (scanner != null) {
            BleServer.setScan(scanner)
        }
        try {
            Log.i("littlePu", "startScan")
            if (ActivityCompat.checkSelfPermission(
                    MainApplication.application,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                leScanner?.startScan(null, settings, leScanCallback)
                return
            }

        } catch (e: Exception) {

        }

    }

    fun stop() {
        try {
            Log.i("littlePu", "stopScan")
            if (ActivityCompat.checkSelfPermission(
                    MainApplication.application,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            leScanner?.stopScan(leScanCallback)
        } catch (e: java.lang.Exception) {

        }

    }
}