package com.vaca.ble_1500_android_project.ble


import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.vaca.ble_1500_android_project.MainApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver
import java.lang.Thread.sleep

class BleDataWorker {
    private var pool: ByteArray? = null
    private val fileChannel = Channel<Int>(Channel.CONFLATED)
    private val connectChannel = Channel<String>(Channel.CONFLATED)
    var myBleDataManager: DataManager? = null
    private val dataScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    private var cmdState = 0;
    var pkgTotal = 0;
    var currentPkg = 0;
    var fileData: ByteArray? = null
    var currentFileName = ""
    var result = 1;
    var currentFileSize = 0
    var lastMill = 0L
    var startMill = 0L


    companion object {
        val fileProgressChannel = Channel<FileProgress>(Channel.CONFLATED)
    }

    data class FileProgress(
        var name: String = "",
        var progress: Int = 0,
        var success: Boolean = false
    )

    private val comeData = object : DataManager.OnNotifyListener {
        override fun onNotify(device: BluetoothDevice?, data: Data?) {
            data?.value?.apply {
                val size = this.size
                Log.e("getit", size.toString())

            }
        }
    }


    fun sendCmd(bs: ByteArray) {
        myBleDataManager?.sendCmd(bs)
    }

    private val connectState = object : ConnectionObserver {
        override fun onDeviceConnecting(device: BluetoothDevice) {

        }

        override fun onDeviceConnected(device: BluetoothDevice) {

        }

        override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {

        }

        override fun onDeviceReady(device: BluetoothDevice) {

        }

        override fun onDeviceDisconnecting(device: BluetoothDevice) {

        }

        override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {

        }

    }


    fun initWorker(context: Context, bluetoothDevice: BluetoothDevice?) {
        try {
            myBleDataManager?.disconnect()?.enqueue()
            sleep(200)
        } catch (ep: Exception) {

        }

        bluetoothDevice?.let {
            myBleDataManager?.connect(it)
                ?.useAutoConnect(false)
                ?.timeout(10000)
                ?.retry(500, 20)
                ?.done {

                    Log.i("BLE", "连接成功了.>>.....>>>>")


                }?.fail(object : FailCallback {
                    override fun onRequestFailed(device: BluetoothDevice, status: Int) {

                    }

                })
                ?.enqueue()
        }
    }

    suspend fun waitConnect() {
        connectChannel.receive()
    }

    fun disconnect() {
        myBleDataManager?.disconnect()?.enqueue()

    }

    init {
        myBleDataManager = DataManager(MainApplication.application)
        myBleDataManager?.setNotifyListener(comeData)
        myBleDataManager?.connectionObserver = connectState
    }

}