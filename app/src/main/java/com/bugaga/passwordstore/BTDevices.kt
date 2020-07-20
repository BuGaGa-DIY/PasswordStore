package com.bugaga.passwordstore

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ProgressBar
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
                btList.setOnItemClickListener { parent, view, position, id ->
                    Toast.makeText(this,"You select ${deviceNames!![position]}",Toast.LENGTH_SHORT).show()
                    val setting = getSharedPreferences("DevicePrefs",Context.MODE_PRIVATE)
                    val edit = setting.edit()
                    edit.putString("MainDeviceMac",deviceMac!![position])
                    edit.apply()
                }
            }
        }



        ScanBT.setOnClickListener {
            val myBT = myBluetooth(applicationContext, Handler())
            myBT.execute()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
    }
}