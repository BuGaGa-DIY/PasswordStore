package com.bugaga.passwordstore

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import com.bugaga.passwordstore.utils.Output
import java.io.IOException
import java.lang.Exception
import java.util.*

class myBluetooth(var context: Context, var handler: Handler) : AsyncTask<Void,Void,Void>() {

    val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    var btAdapter : BluetoothAdapter? = null
    var btSocket : BluetoothSocket? = null
    init {
        //connect()
        execute()
    }

    @Suppress("UNREACHABLE_CODE")
    override fun doInBackground(vararg params: Void?): Void? {
        connect()
        return null
    }

    fun connect():Boolean{
        btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null){
            handler.sendMessage(Message.obtain(handler,0))
            return false
        }
        Output().WriteLine("BT adapter not null")
        if (!btAdapter!!.isEnabled) {
            handler.sendMessage(Message.obtain(handler, 1))
            return false
        }
        Output().WriteLine("BT is enabled")
        val deviceMac = context.getSharedPreferences("DevicePrefs",Context.MODE_PRIVATE).getString("MainDeviceMac","")
        Output().WriteLine("device mac: $deviceMac")
        if (deviceMac == "") {
            handler.sendMessage(Message.obtain(handler,2))
            return false
        }
        val device = btAdapter!!.getRemoteDevice(deviceMac)
        btAdapter!!.cancelDiscovery()
        try {
            btSocket = device.createInsecureRfcommSocketToServiceRecord(uuid)
            Output().WriteLine("Socket created")
            btSocket?.connect()
            Output().WriteLine("Socket connected")
            return true
        }catch (ex : IOException){
            Output().WriteLine("socket opening fail: ${ex.message}")
            return false
        }
    }

    fun sendData(data : String){
        if (btSocket == null){
            handler.sendMessage(Message.obtain(handler,3))
            return
        }
        else{
            try {
                btSocket?.outputStream?.write(data.toByteArray())
            }catch (ex :IOException){
                Output().WriteLine("Data sending fail: ${ex.message}")
            }

        }
    }

    fun isReady():Boolean{
        if (btSocket != null ) {
            return btSocket!!.isConnected
        }
        else return false
    }

    fun close(){
        try {
            btSocket?.close()
            Output().WriteLine("Socket closed")
        }catch (e : Exception){
            Output().WriteLine("Socket closing fail: ${e.message}")
        }

    }
}