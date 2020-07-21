package com.bugaga.passwordstore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

class myAdapter(val context:Context,val dataList: List<String>, val sender: View.OnClickListener):BaseAdapter() {
    private val mInflator: LayoutInflater
    init {
        this.mInflator = LayoutInflater.from(context)
    }

    private class ViewHolder(row: View?){
        var name: TextView? = null
        var sendBt : Button? = null
        init {
            name = row?.findViewById(R.id.adapterNameText)
            sendBt = row?.findViewById(R.id.sendToBt)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View?
        var viewHolder: ViewHolder
        if (convertView == null){
            view = mInflator.inflate(R.layout.adapret_layout,null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.name?.text = dataList[position]
        /*viewHolder.sendBt?.setOnClickListener {
            Toast.makeText(context,dataList[position],Toast.LENGTH_SHORT).show()
        }*/
        viewHolder.sendBt?.setOnClickListener(sender)
        return view as View
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataList.size
    }
}