package com.bugaga.passwordstore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.AlarmClock
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.new_pass_leyout.view.*
import java.util.concurrent.Executor
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    lateinit var passList : ListView
    lateinit var passBT : View

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passList = findViewById(R.id.PassList)
        passBT = findViewById(R.id.AddPassBT)


        var passNameArray = FileWriter(applicationContext).getAllNames()//mutableListOf<String>()
        val passAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_list_item_1,passNameArray)
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
                val handler : Handler = object : Handler(Looper.getMainLooper()){
                    override fun handleMessage(msg: Message) {
                        when(msg.what){
                            0->{
                                passNameArray.clear()
                                passNameArray.addAll(FileWriter(applicationContext).getAllNames())
                                passAdapter.notifyDataSetChanged()
                            }
                            else->{}
                        }
                    }
                }
                Dialogs().addPassDialog(this,handler,input.text.toString())
            }
            builder.show()

        }

        passList.setOnItemClickListener { parent, view, position, id ->
            val context = this
            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int,
                                                       errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(applicationContext,
                            "Authentication error: $errString", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle(FileWriter(applicationContext).getPass(passNameArray[position]))
                        builder.setPositiveButton("Ok"){dialog, which ->
                            dialog.dismiss()
                        }
                        builder.setNegativeButton("Изменить"){dialog, which ->
                            Dialogs().addPassDialog(context,null,passNameArray[position])
                        }
                        builder.setNeutralButton("Удалить"){dialog, which ->
                            val reallyBuilder = AlertDialog.Builder(context)
                            reallyBuilder.setTitle("Точно хочешь удалить пароль?")
                            reallyBuilder.setPositiveButton("ДА!") { dialog1, which1 ->
                                FileWriter(applicationContext).deletePass(passNameArray[position])
                                passNameArray.clear()
                                passNameArray.addAll(FileWriter(applicationContext).getAllNames())
                                passAdapter.notifyDataSetChanged()
                                dialog1.dismiss()
                            }
                            reallyBuilder.setNeutralButton("НЕТ"){dialog1, which1 ->
                                dialog1.dismiss()
                            }
                            reallyBuilder.show()
                            dialog.dismiss()
                        }
                        builder.show()

                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(applicationContext, "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Подтверждение")
                .setNegativeButtonText("Отмена")
                .build()

            biometricPrompt.authenticate(promptInfo)
        }
    }
}