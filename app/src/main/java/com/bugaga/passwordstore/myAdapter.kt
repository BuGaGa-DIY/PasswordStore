package com.bugaga.passwordstore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView

class myAdapter(val context:Context,val dataList: List<String>):BaseAdapter() {
    private val mInflator: LayoutInflater
    init {
        this.mInflator = LayoutInflater.from(context)
    }

    private class ViewHolder(row: View?){
        var name: TextView? = null

        init {
            name = row?.findViewById(R.id.adapterNameText)
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