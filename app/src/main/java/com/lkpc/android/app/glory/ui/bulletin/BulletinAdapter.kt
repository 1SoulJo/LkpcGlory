package com.lkpc.android.app.glory.ui.bulletin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.constants.WebUrls
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.ui.basic_webview.BasicWebviewActivity
import java.text.SimpleDateFormat
import java.util.*

class BulletinAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val _typeItem: Int = 0
    private val _typeLoading: Int = 1

    var isLoading: Boolean = false

    var bulletins: List<BaseContent?> = mutableListOf()

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.bulletin_title)
        var date: TextView = view.findViewById(R.id.bulletin_date)
    }

    class LoadingViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == _typeItem) {
            return ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_bulletin, parent, false)
            )
        }
        return LoadingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.loading_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val bulletin = bulletins[position]!!

            holder.title.text = bulletin.title

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.CANADA)
            val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
            holder.date.text = newFormat.format(dateFormat.parse(bulletin.dateCreated!!)!!)

            holder.itemView.setOnClickListener {
                val i = Intent(holder.itemView.context, BasicWebviewActivity::class.java)
                i.putExtra("url", WebUrls.PDF_BASE.format(bulletin.files!![0].toString()))
                holder.itemView.context.startActivity(i)
            }
        }
    }

    override fun getItemCount(): Int {
        return bulletins.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (bulletins[position] == null) _typeLoading else _typeItem
    }

}