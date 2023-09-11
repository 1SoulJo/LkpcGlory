package com.lkpc.android.app.glory.ui.qr_code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.entity.QrInfo

class QrCodeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.qr_info_id)
        val info: TextView = view.findViewById(R.id.qr_info)
        val date: TextView = view.findViewById(R.id.qr_date)
    }

    var qrInfoList: ArrayList<QrInfo> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_qr_info, parent,
                false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val info = qrInfoList[position]
            holder.id.text = "${position + 1}"
            holder.info.text = info.info
            holder.date.text = info.date
        }
    }

    override fun getItemCount(): Int {
        return qrInfoList.size
    }

    fun addInfo(item: QrInfo) {
        qrInfoList.add(item)
        notifyDataSetChanged()
    }

    fun clearList() {
        qrInfoList.clear()
        notifyDataSetChanged()
    }
}