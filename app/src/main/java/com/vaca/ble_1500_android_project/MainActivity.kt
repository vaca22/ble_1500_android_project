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
import com.vaca.ble_1500_android_project.ble.BleCmd
import com.vaca.ble_1500_android_project.databinding.ActivityMainBinding
import com.vaca.ble_1500_android_project.utils.NetCmd
import com.vaca.ble_1500_android_project.utils.PathUtil
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class MainActivity : AppCompatActivity() {
    companion object {
        val bleHandler = Handler()
        lateinit var mBluetoothAdapter: BluetoothAdapter
        var sendChannel = Channel<O2ringSet>(Channel.UNLIMITED)
        val receiveChannel = Channel<Boolean>(Channel.UNLIMITED)
    }

    lateinit var binding:ActivityMainBinding
    lateinit var  bytes:ByteArray


    var currentIndex=0;
    var total=0;




    var sendJobActivate = true



    data class O2ringSet(val cmd: ByteArray, val index:Int)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.send.setOnClickListener {
            Log.e("fuck","fuckyou")
            currentIndex=0;

            BleServer.dataScope.launch {
                val mtu=180
                var n=total/mtu;
                if(n==0){
                    val k=0
                    sendChannel.send(O2ringSet(bytes.copyOfRange(k*mtu,total),k*mtu))
                }else{


                    if(n*mtu!=total){
                        val ll=total-n*mtu
                        for(k in 0 until n){
                            sendChannel.send(O2ringSet(bytes.copyOfRange(k*mtu,(k+1)*mtu),k*mtu))
                        }
                        sendChannel.send(O2ringSet(bytes.copyOfRange(n*mtu,total),n*mtu))
                    }else{
                        Log.e("fuck","fuckyoufuckyout")
                        for(k in 0 until n){
                            sendChannel.send(O2ringSet(bytes.copyOfRange(k*mtu,(k+1)*mtu),k*mtu))
                        }
                    }
                }


                while (sendJobActivate) {
                    val k = sendChannel.receive()
                    do {
                        BleServer.worker.sendCmd(BleCmd.sendOtaData(k.cmd,k.index,total));
                        if (!sendJobActivate) {
                            break
                        }
                    } while (withTimeoutOrNull(5000) {
                            receiveChannel.receive()
                        } == null)
                }
            }
         //   BleServer.worker.sendText("qwertyuioplkjhgfdsazxcvbnm")

        }

        binding.getFile.setOnClickListener {
            BleServer.dataScope.launch {
                val nx=NetCmd.getFile()
                nx?.let {
                    bytes=it
                    total=it.size
                    Log.e("fuckSize",nx.size.toString())
                    File(PathUtil.getPathX("fuck.bin")).writeBytes(it)
                }
            }
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