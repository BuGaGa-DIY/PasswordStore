package com.bugaga.passwordstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.pass_preview_layout.view.*
import java.util.concurrent.Executor
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {

    lateinit var passList : ListView
    lateinit var passBT : View

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var myBt : myBluetooth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passList = findViewById(R.id.PassList)
        passBT = findViewById(R.id.AddPassBT)


        val sender = object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity,"Lol",Toast.LENGTH_SHORT).show()
            }
        }
        val passNameArray = FileWriter(applicationContext).getAllNames()
        val passAdapter = myAdapter(applicationContext, passNameArray,sender)
        passList.adapter = passAdapter
        passBT.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Введите названпя для пароля")
            val input = EditText(this)
            builder.setView(input)
            builder.setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            builder.setPositiveButton("OK") { dialog, which ->
                val handler: Handler = object : Handler(Looper.getMainLooper()) {
                    override fun handleMessage(msg: Message) {
                        when (msg.what) {
                            0 -> {
                                passNameArray.clear()
                                passNameArray.addAll(FileWriter(applicationContext).getAllNames())
                                passAdapter.notifyDataSetChanged()
                            }
                            else -> {
                            }
                        }
                    }
                }
                Dialogs().addPassDialog(this, handler, input.text.toString())
            }
            builder.show()

        }

        passList.setOnItemClickListener { parent, view, position, id ->
            val context = this
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(
                            applicationContext,
                            "Authentication error: $errString", Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        val builder = AlertDialog.Builder(context)
                        val inflater = LayoutInflater.from(applicationContext)
                            .inflate(R.layout.pass_preview_layout, null)
                        inflater.perviewName.text = Editable.Factory.getInstance().newEditable(
                            passNameArray[position]
                        )
                        val loginPass =
                            FileWriter(applicationContext).getLoginPass(passNameArray[position])
                        if (loginPass != "") {
                            inflater.previewLogin.text =
                                Editable.Factory.getInstance().newEditable(
                                    loginPass.substring(0, loginPass.indexOf("|"))
                                )
                            inflater.previewPass.text =
                                Editable.Factory.getInstance().newEditable(
                                    loginPass.substring(loginPass.indexOf("|") + 1)
                                )

                            /*inflater.previewPass.setOnClickListener {
                                val oldPass = FileWriter(applicationContext).getPassHistory(passNameArray[position])
                                if (oldPass.size > 1){
                                    val oldPassBuilder = AlertDialog.Builder(context)
                                    oldPassBuilder.setTitle("Предыдущие пароли")
                                    var str = ""
                                    oldPass.forEach { str += it + "\n" }
                                    oldPassBuilder.setMessage(str)
                                    oldPassBuilder.setPositiveButton("ok"){d, w ->
                                        d.dismiss()
                                    }
                                    oldPassBuilder.show()
                                }
                            }*/
                        }
                        builder.setView(inflater)
                        builder.setPositiveButton("Ok") { dialog, which ->
                            dialog.dismiss()
                        }
                        builder.setNegativeButton("Изменить") { dialog, which ->
                            Dialogs().addPassDialog(context, null, passNameArray[position])
                        }
                        builder.setNeutralButton("Удалить") { dialog, which ->
                            val reallyBuilder = AlertDialog.Builder(context)
                            reallyBuilder.setTitle("Точно хочешь удалить пароль?")
                            reallyBuilder.setPositiveButton("ДА!") { dialog1, which1 ->
                                FileWriter(applicationContext).deletePass(passNameArray[position])
                                passNameArray.clear()
                                passNameArray.addAll(FileWriter(applicationContext).getAllNames())
                                passAdapter.notifyDataSetChanged()
                                dialog1.dismiss()
                            }
                            reallyBuilder.setNeutralButton("НЕТ") { dialog1, which1 ->
                                dialog1.dismiss()
                            }
                            reallyBuilder.show()
                            dialog.dismiss()
                        }
                        builder.setCancelable(false)
                        builder.show()

                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(
                            applicationContext, "Authentication failed",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Подтверждение")
                .setNegativeButtonText("Отмена")
                .build()

            biometricPrompt.authenticate(promptInfo)
        }

        passList.setOnItemLongClickListener { parent, view, position, id ->

            myBt?.sendData(passNameArray[position])
            return@setOnItemLongClickListener(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuDevices->{
                //Toast.makeText(this,"menu item cliced",Toast.LENGTH_SHORT).show()
                val startIntent = Intent(applicationContext,BTDevices::class.java)
                startActivity(startIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        myBt = myBluetooth(applicationContext, Handler())
    }

    override fun onPause() {
        super.onPause()
        myBt?.close()
    }
    override fun onDestroy() {
        super.onDestroy()
        myBt?.close()
    }
}