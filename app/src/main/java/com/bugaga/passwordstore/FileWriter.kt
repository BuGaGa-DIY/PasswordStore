package com.bugaga.passwordstore

import android.content.Context
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.FileWriter

class FileWriter(val context: Context) {


    fun addPass(name:String,pass:String){
        var all = readAll()
        val path = context.getExternalFilesDir("pass")
        if (path != null && !path.exists()) path.mkdir()
        try {
            val file = File(path,"pass.txt")
            if (all.contains(name)){
                val writer = FileWriter(file,false)
                val index1 = all.indexOf(name)+name.length+1
                val tmp = all.substring(0,index1) + pass+";"+all.substring(index1)
                writer.write(tmp)
                writer.flush()
                writer.close()
            }else{
                val writer = FileWriter(file,true)
                writer.append("$name:$pass;\n")
                writer.flush()
                writer.close()
            }
        }catch (e: FileNotFoundException){}
    }

    fun readAll():String{
        var result = ""
        val path = context.getExternalFilesDir("pass")
        if (path != null && !path.exists()) return ""
        try {
            val file = File(path,"pass.txt")
            val reader = FileReader(file)
            while (reader.ready()) result += reader.read().toChar()
            reader.close()
        }catch (e: FileNotFoundException){}
        return result
    }

    fun getAllNames():MutableList<String>{
        var list = mutableListOf<String>()
        var all = readAll()
        while (all != ""){
            var ind = all.indexOf(":")
            if (ind > -1){
                list.add(all.substring(0,ind))
                ind = all.indexOf("\n")+1
                all = all.substring(ind)
            }
        }
        return list
    }

    fun getPass(name: String):String{
        val all = readAll()
        if (all.contains(name)){
            val startInd = all.indexOf(name)+name.length + 1
            val endInd = all.substring(startInd).indexOf(";") + startInd
            return all.substring(startInd,endInd)
        }else return ""
    }

    fun getPassHistory(name: String):MutableList<String>{
        val list = mutableListOf<String>()
        val all = readAll()
        if (all.contains(name)){
            val startInd = all.indexOf(name)+name.length + 1
            val endInd = all.substring(startInd).indexOf("\n") + startInd
            var passes = all.substring(startInd,endInd)
            while (passes != ""){
                val ind = passes.indexOf(";")
                if (ind > -1){
                    list.add(passes.substring(0,ind))
                    passes = passes.substring(ind)
                }
            }
        }
        return list
    }

    fun deletePass(name: String){
        var all = readAll()
        if (all.contains(name)){
            val path = context.getExternalFilesDir("pass")
            val file = File(path,"pass.txt")
            val writer = FileWriter(file,false)
            val sInd = all.indexOf(name)
            val eInd = all.substring(sInd).indexOf("\n")+sInd+1
            all = all.substring(0,sInd)+all.substring(eInd)
            writer.write(all)
            writer.flush()
            writer.close()
        }
    }
}