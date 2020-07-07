package com.bugaga.passwordstore

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.bt_devices_layout.*

class BTDevices: AppCompatActivity() {

    var deviceNames :MutableList<String>? = null
    var deviceMac :MutableList<String>? = null
    var listAdapter :ArrayAdapter<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bt_devices_layout)

        val toolBar = supportActionBar
        toolBar?.title = "BT Devices"
        deviceNames = mutableListOf()
        deviceMac = mutableListOf()
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter == null){
            Toast.makeText(this,"Your device does not support Bluetooth",Toast.LENGTH_LONG).show()
        }else{
            if (btAdapter.isEnabled == false){
                Toast.makeText(this,"Please turn ON Bluetooth!",Toast.LENGTH_LONG).show()
            }else{
                val pairedDevices = btAdapter.bondedDevices
                pairedDevices.forEach {
                    deviceNames!!.add(it.name)
                    deviceMac!!.add(it.address)
                }
                listAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,deviceNames!!)
                btList.adapter = listAdapter
            }
        }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver,filter)
        btAdapter.startDiscovery()
    }

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when(action){
                BluetoothDevice.ACTION_FOUND->{
                    val devices: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if(devices != null){
                        Log.w("myLog","New Device fount ${devices.name}")
                        deviceNames?.add(devices.name)
                        deviceMac?.add(devices.address)
                        listAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}