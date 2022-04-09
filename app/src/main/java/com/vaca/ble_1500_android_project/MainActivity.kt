package com.vaca.ble_1500_android_project

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vaca.ble_1500_android_project.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        val bleHandler = Handler()
        lateinit var mBluetoothAdapter: BluetoothAdapter
    }

    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.send.setOnClickListener {
            Log.e("fuck","fuckyou")
        }


        val requestVoicePermission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            BleSS()
        }






        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestVoicePermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        } else {
            BleSS()
        }

    }

    fun BleSS() {
        BleServer.dataScope.launch {
            delay(3000)
            ScanBle()
        }

    }

    suspend fun ScanBle() {
        delay(1000)
        BleServer.setScanCallBack(MainApplication.application)

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        //获取BluetoothAdapter
        mBluetoothAdapter = bluetoothManager.adapter

        if (!mBluetoothAdapter.isEnabled) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            mBluetoothAdapter.enable()
        } else {
            bleHandler.postDelayed(bleTask, 1000)
        }
    }

    val bleTask = object : Runnable {
        override fun run() {
            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            mBluetoothAdapter = bluetoothManager.adapter
            val scanner = mBluetoothAdapter.bluetoothLeScanner
            if (scanner != null) {
                BleServer.setScan(scanner)
                BleServer.scan.start()
                Log.i("littlePu", "scanStart")
            } else {

            }


        }

    }
}