package com.vaca.ble_1500_android_project.ble


import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.vaca.ble_1500_android_project.BleServer
import com.vaca.ble_1500_android_project.MainActivity
import com.vaca.ble_1500_android_project.MainApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver

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



    data class FileProgress(
        var name: String = "",
        var progress: Int = 0,
        var success: Boolean = false
    )

    private val comeData = object : DataManager.OnNotifyListener {
        override fun onNotify(device: BluetoothDevice?, data: Data?) {
            data?.value?.apply {
                val size = this.size
                Log.e("getit",byteArray2String(this))
                BleServer.dataScope.launch {
                    MainActivity.receiveChannel.send(true)
                }

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
            BleServer.connectFlag = false
            Log.e("fuck","断开了")
            BleServer.scan.start()
        }

    }


    fun initWorker(context: Context, bluetoothDevice: BluetoothDevice?) {
        bluetoothDevice?.let {
            myBleDataManager?.connect(it)
                ?.useAutoConnect(false)
                ?.timeout(10000)
                ?.retry(500, 20)
                ?.done {

                    Log.i("BLE", "连接成功了.>>.....>>>>")
                    BleServer.scan.stop()

                }?.fail(object : FailCallback {
                    override fun onRequestFailed(device: BluetoothDevice, status: Int) {

                    }

                })
                ?.enqueue()
        }
    }

    fun byteArray2String(byteArray: ByteArray): String {
        var fuc = ""
        for (b in byteArray) {
            val st = String.format("%02X", b)
            fuc += ("$st  ");
        }
        return fuc
    }

    fun sendText(s:String){
        myBleDataManager?.sendCmd(s.toByteArray())
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