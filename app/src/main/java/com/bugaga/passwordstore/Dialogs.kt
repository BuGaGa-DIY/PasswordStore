package com.bugaga.passwordstore

import android.content.Context
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.filter_layout.view.*
import kotlinx.android.synthetic.main.new_pass_leyout.view.*
import kotlin.random.Random

class Dialogs() {
    fun addPassDialog(context: Context,handler:android.os.Handler?,name: String){
        val loginPass = FileWriter(context).getLoginPass(name)
        var mainDialog : AlertDialog? = null
        val passBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context).inflate(R.layout.new_pass_leyout,null)
        if (loginPass!=""){
            inflater.editLogin.text = Editable.Factory.getInstance().newEditable(
                loginPass.substring(0,loginPass.indexOf("|")))
            inflater.editPass.text = Editable.Factory.getInstance().newEditable(
                loginPass.substring(loginPass.indexOf("|")+1))
        }
        inflater.newPassCancelBT.setOnClickListener {
            mainDialog!!.dismiss()
        }
        inflater.newPassRandomBT.setOnClickListener {
            val getPassHandler:android.os.Handler = object : android.os.Handler(Looper.getMainLooper()){
                override fun handleMessage(msg: Message) {
                    when(msg.what){
                        0->{
                            inflater.editPass.text = Editable.Factory.getInstance().newEditable(msg.obj.toString())
                        }
                    }
                }
            }
            getPassFromFilter(context,getPassHandler)
        }
        inflater.newPassOkBT.setOnClickListener {
            mainDialog!!.dismiss()
            FileWriter(context).addPass(name,inflater.editPass.text.toString(),inflater.editLogin.text.toString())
            handler?.sendMessage(Message.obtain(handler,0))
        }
        passBuilder.setView(inflater)
        mainDialog = passBuilder.create()
        mainDialog.show()
    }

    private fun getPassFromFilter(context: Context,handler: android.os.Handler){
        var mainDialog : AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context).inflate(R.layout.filter_layout,null)
        inflater.maxLength.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                inflater.maxSizeTextBox.text = Editable.Factory.getInstance().newEditable(progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        inflater.switch09.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                inflater.switchAZ.isEnabled = true
                inflater.switchaz.isEnabled = true
                inflater.switchSpecial.isEnabled = true
            }else{
                if (!inflater.switchAZ.isChecked && !inflater.switchaz.isChecked) inflater.switchSpecial.isEnabled = false
                else if (!inflater.switchAZ.isChecked && !inflater.switchSpecial.isChecked) inflater.switchaz.isEnabled = false
                else if (!inflater.switchaz.isChecked && !inflater.switchSpecial.isChecked) inflater.switchAZ.isEnabled = false
            }
        }
        inflater.switchaz.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                inflater.switchAZ.isEnabled = true
                inflater.switch09.isEnabled = true
                inflater.switchSpecial.isEnabled = true
            }else{
                if (!inflater.switchAZ.isChecked && !inflater.switch09.isChecked) inflater.switchSpecial.isEnabled = false
                else if (!inflater.switchAZ.isChecked && !inflater.switchSpecial.isChecked) inflater.switch09.isEnabled = false
                else if (!inflater.switch09.isChecked && !inflater.switchSpecial.isChecked) inflater.switchAZ.isEnabled = false
            }
        }
        inflater.switchAZ.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                inflater.switch09.isEnabled = true
                inflater.switchaz.isEnabled = true
                inflater.switchSpecial.isEnabled = true
            }else{
                if (!inflater.switch09.isChecked && !inflater.switchaz.isChecked) inflater.switchSpecial.isEnabled = false
                else if (!inflater.switch09.isChecked && !inflater.switchSpecial.isChecked) inflater.switchaz.isEnabled = false
                else if (!inflater.switchaz.isChecked && !inflater.switchSpecial.isChecked) inflater.switch09.isEnabled = false
            }
        }
        inflater.switchSpecial.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                inflater.switch09.isEnabled = true
                inflater.switchaz.isEnabled = true
                inflater.switchAZ.isEnabled = true
            }else{
                if (!inflater.switch09.isChecked && !inflater.switchaz.isChecked) inflater.switchAZ.isEnabled = false
                else if (!inflater.switch09.isChecked && !inflater.switchAZ.isChecked) inflater.switchaz.isEnabled = false
                else if (!inflater.switchaz.isChecked && !inflater.switchAZ.isChecked) inflater.switch09.isEnabled = false
            }
        }
        inflater.filterOkBT.setOnClickListener {
            var pass = ""
            var cnt = 0
            while (cnt < inflater.maxLength.progress){
                val type = Random.nextInt(0,4)
                when(type){
                    0->{
                        if (inflater.switch09.isChecked) {
                            pass += Random.nextInt(48, 57).toChar()
                            cnt++
                        }
                    }
                    1->{
                        if (inflater.switchAZ.isChecked) {
                            pass += Random.nextInt(65,90).toChar()
                            cnt++
                        }
                    }
                    2->{
                        if (inflater.switchaz.isChecked) {
                            pass += Random.nextInt(97,122).toChar()
                            cnt++
                        }
                    }
                    3->{
                        if (inflater.switchSpecial.isChecked) {
                            pass += Random.nextInt(33,47).toChar()
                            cnt++
                        }
                    }
                }
            }
            handler.sendMessage(Message.obtain(handler,0,pass))
            mainDialog?.dismiss()
        }
        builder.setView(inflater)
        mainDialog = builder.create()
        mainDialog.show()
    }
}