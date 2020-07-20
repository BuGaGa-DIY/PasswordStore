package com.bugaga.passwordstore

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import com.bugaga.passwordstore.utils.Output
import java.io.IOException
import java.util.*

class myBluetooth(var context: Context, var handler: Handler) : AsyncTask<Void,Void,Void>() {

    val uuid: UUID = UUID.fromString("8989063a-c9af-463a-b3f1-f21d9b2b827b")
    var btAdapter : BluetoothAdapter? = null
    var btSocket : BluetoothSocket? = null
    override fun doInBackground(vararg params: Void?): Void {
        btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null) handler.sendMessage(Message.obtain(handler,0))
        else{
            Output().WriteLine("BT adapter not null")
            if (!btAdapter!!.isEnabled) handler.sendMessage(Message.obtain(handler,1))
            else{
                Output().WriteLine("BT is enabled")
                val deviceMac = context.getSharedPreferences("DevicePrefs",Context.MODE_PRIVATE).getString("MainDeviceMac","")
                Output().WriteLine("device mac: $deviceMac")
                if (deviceMac == "") handler.sendMessage(Message.obtain(handler,2))
                else{
                    val device = btAdapter!!.getRemoteDevice(deviceMac)

                    try {
                        btSocket = device.createInsecureRfcommSocketToServiceRecord(uuid)
                        Output().WriteLine("Socket created")
                        btSocket?.connect()
                        Output().WriteLine("Socket connected")
                        val outputStream = btSocket?.outputStream
                        outputStream?.write("Test msg".toByteArray())
                        Output().WriteLine("Message sent")
                    }catch (ex : IOException){
                        Output().WriteLine("socket opening fail")
                    }
                }
            }

        }

        return null!!
    }
}