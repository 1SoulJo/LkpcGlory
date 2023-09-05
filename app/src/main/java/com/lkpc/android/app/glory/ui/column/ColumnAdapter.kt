package com.lkpc.android.app.glory.ui.column

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.data.NoteDatabase
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.ui.detail.DetailActivity
import java.text.SimpleDateFormat
import java.util.*

class ColumnAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val _typeItem: Int = 0
    private val _typeLoading: Int = 1

    var isLoading: Boolean = false

    var columns: List<BaseContent?> = mutableListOf()

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var tvColumnTitle: TextView = view.findViewById(R.id.column_title)
        var tvColumnDate: TextView = view.findViewById(R.id.column_date)
        var ivNote: ImageView = view.findViewById(R.id.column_note)
    }

    class LoadingViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == _typeItem) {
            return ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_column, parent, false)
            )
        }
        return LoadingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.loading_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val column = columns[position]!!

            holder.tvColumnTitle.text = column.title

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.CANADA)
            val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
            holder.tvColumnDate.text = newFormat.format(dateFormat.parse(column.dateCreated!!)!!)

            val db = NoteDatabase.getDatabase(context = holder.itemView.context)
            db.noteDao().loadByContentId(column.id!!).observe(
                holder.itemView.context as LifecycleOwner
            ) { note ->
                val i = Intent(holder.itemView.context, DetailActivity::class.java)
                i.putExtra("data", Gson().toJson(columns[position]))

                if (note != null) {
                    holder.ivNote.visibility = View.VISIBLE
                    i.putExtra("noteId", note.id)
                } else {
                    holder.ivNote.visibility = View.GONE
                }

                holder.itemView.setOnClickListener {
                    holder.itemView.context.startActivity(i)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return columns.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (columns[position] == null) _typeLoading else _typeItem
    }
}