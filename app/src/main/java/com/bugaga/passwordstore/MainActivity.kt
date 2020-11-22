package com.bugaga.passwordstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.bugaga.passwordstore.utils.Output
import com.bugaga.passwordstore.utils.myFingerPrint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.pass_preview_layout.view.*

class MainActivity : AppCompatActivity() {

    lateinit var toolBar: Toolbar
    lateinit var passList : ListView
    lateinit var passBT : View
    lateinit var myHandler: Handler
    var passAdapter : myAdapter? = null
    var passNameArray : MutableList<String>? = null

    //private lateinit var executor: Executor
    //private lateinit var biometricPrompt: BiometricPrompt
    //private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private var myBt : myBluetooth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolBar = findViewById(R.id.toolbar)
        setSupportActionBar(toolBar)

        myHandler = Handler(Handler.Callback { msg ->

            when(msg.what){
                3->{
                    toolbarText.setTextColor(ContextCompat.getColor(this,R.color.colorConnected))
                    toolbarText.text = "Connected"
                    //Toast.makeText(this,"Connected to BT device",Toast.LENGTH_SHORT).show()
                }
                5->{
                    toolbarText.setTextColor(ContextCompat.getColor(this,R.color.colorDisconnected))
                    toolbarText.text = "Disconnected"
                }
            }

            return@Callback true
        })


        passList = findViewById(R.id.PassList)
        passBT = findViewById(R.id.AddPassBT)
        //var passAdapter : myAdapter? = null

        val passNameArray = FileWriter(applicationContext).getAllNames()

        passAdapter = myAdapter(applicationContext, passNameArray)
        passList.adapter = passAdapter
        FreeEnterFAB.setOnClickListener {
            startFreeEnter()
        }
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
                                passAdapter?.notifyDataSetChanged()
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
            val getPass = myFingerPrint(applicationContext,this)
            {showPassDialog(passNameArray[position])}
            getPass.Title = "Show pass confirmation"
            getPass.show()
        }

        passList.setOnItemLongClickListener { parent, view, position, id ->

            val getAccess = myFingerPrint(applicationContext,this)
            {sendDataToBT(passNameArray[position])}
            getAccess.Title = "Send data to BT"
            getAccess.show()
            return@setOnItemLongClickListener(true)
        }
    }

    private fun startFreeEnter() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Free Enter")
        var edit = EditText(applicationContext)
        edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null)myBt?.sendData(s[start].toString())
                //edit.text = Editable.Factory.getInstance().newEditablef

            }

            override fun afterTextChanged(s: Editable?) {
                //if (s != null)myBt?.sendData(s.toString())
                s?.clear()
                //edit.text.clear()
            }
        })
        builder.setView(edit)
        builder.show()
    }

    fun sendDataToBT(passName: String){
        val loginPass =
            FileWriter(applicationContext).getLoginPass(passName)
        myBt?.sendData(
            loginPass.substring(loginPass.indexOf("|") + 1))

        Toast.makeText(this,
        "Pass sent",
        Toast.LENGTH_SHORT).show()
    }

    fun showPassDialog(passName: String){
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(applicationContext)
            .inflate(R.layout.pass_preview_layout, null)
        inflater.perviewName.text = Editable.Factory.getInstance().newEditable(
            passName
        )
        val loginPass =
            FileWriter(applicationContext).getLoginPass(passName)
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
            Dialogs().addPassDialog(this, null, passName)
        }
        builder.setNeutralButton("Удалить") { dialog, which ->
            val reallyBuilder = AlertDialog.Builder(this)
            reallyBuilder.setTitle("Точно хочешь удалить пароль?")
            reallyBuilder.setPositiveButton("ДА!") { dialog1, which1 ->
                FileWriter(applicationContext).deletePass(passName)
                passNameArray?.clear()
                passNameArray?.addAll(FileWriter(applicationContext).getAllNames())
                passAdapter?.notifyDataSetChanged()
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



    var _isRunning = false
    override fun onResume() {
        super.onResume()
        myBt = myBluetooth(applicationContext, myHandler)
        val thread = Thread(Runnable {
            while (_isRunning){
                if (myBt == null ){
                    runOnUiThread {
                        toolbarText.text = "Disconnected"
                        toolbarText.setTextColor(ContextCompat.getColor(this,R.color.colorDisconnected))
                    }
                }
                Output().WriteLine("Bt state: ${myBt?.isReady()}")
                Thread.sleep(1000)
            }
        })
        if (!_isRunning)_isRunning = true
        //thread.start()
    }

    override fun onPause() {
        super.onPause()
        //_isRunning = false
        myBt?.close()
        toolbarText.text = "Disconnected"
        toolbarText.setTextColor(ContextCompat.getColor(this,R.color.colorDisconnected))
    }
    override fun onDestroy() {
        super.onDestroy()
        myBt?.close()
        _isRunning = false
    }
}