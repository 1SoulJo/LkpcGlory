package com.lkpc.android.app.glory.ui.fellow_news

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.ui.detail.DetailActivity
import kotlinx.android.synthetic.main.list_item_fellow_news.view.*
import java.text.SimpleDateFormat
import java.util.*

class FellowNewsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val _typeItem: Int = 0
    private val _typeLoading: Int = 1

    var isLoading: Boolean = false

    var fellowNews: List<BaseContent?> = mutableListOf()

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var title: TextView = view.fellow_news_title
        var date: TextView = view.fellow_news_date
    }

    class LoadingViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == _typeItem) {
            return ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_fellow_news, parent, false)
            )
        }
        return LoadingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.loading_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = fellowNews[position]!!

            holder.title.text = item.title

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.CANADA)
            val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
            holder.date.text = newFormat.format(dateFormat.parse(item.dateCreated!!)!!)

            holder.itemView.setOnClickListener {
                val i = Intent(holder.itemView.context, DetailActivity::class.java)
                i.putExtra("data", Gson().toJson(fellowNews[position]))
                holder.itemView.context.startActivity(i)
            }
        }
    }

    override fun getItemCount(): Int {
        return fellowNews.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (fellowNews[position] == null) _typeLoading else _typeItem
    }
}