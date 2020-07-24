package com.bugaga.passwordstore.utils

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor


class myFingerPrint(val context : Context, localThis : FragmentActivity, var mainFun : () -> Unit) {

    private var executor: Executor
    private var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    var Title: String = ""
    var NegotivButtonText: String = "Cancel"
    init {
        executor = ContextCompat.getMainExecutor(context)
        biometricPrompt = BiometricPrompt(localThis,executor, object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Toast.makeText(context,"Canceled", Toast.LENGTH_SHORT).show()

            }

            override fun onAuthenticationFailed() {
                Toast.makeText(context,"Auth Fail",Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                mainFun()
            }
        })


    }

    fun show(){
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(Title)
            .setNegativeButtonText(NegotivButtonText)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}