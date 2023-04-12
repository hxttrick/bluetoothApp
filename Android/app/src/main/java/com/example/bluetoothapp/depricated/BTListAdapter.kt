package com.example.bluetoothapp.depricated

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothapp.R

class BTListAdapter(val context: Context, val itemTitleList: ArrayList<String>, val itemAddressList: ArrayList<String>, val deviceList: ArrayList<BluetoothDevice>) :
    RecyclerView.Adapter<BTListAdapter.ViewHolder>(){

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(pos: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.device_list_item,
                parent,
                false),
                mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItemTitle = itemTitleList[position]
        val currentItemAddress = itemAddressList[position]
        holder.itemTitle.text = currentItemTitle
        holder.itemAddress.text = currentItemAddress
    }

    override fun getItemCount(): Int {

        return itemTitleList.size
    }

    class ViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val itemTitle : TextView = itemView.findViewById(R.id.btListItemTitle)
        val itemAddress : TextView = itemView.findViewById(R.id.btListItemAddress)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

}