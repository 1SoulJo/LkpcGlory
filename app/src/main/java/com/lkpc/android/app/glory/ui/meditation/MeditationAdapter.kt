package com.lkpc.android.app.glory.ui.meditation

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
import com.lkpc.android.app.glory.constants.WebUrls
import com.lkpc.android.app.glory.data.NoteDatabase
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.ui.basic_webview.BasicWebviewActivity
import com.lkpc.android.app.glory.ui.detail.DetailActivity
import java.text.SimpleDateFormat
import java.util.*

class MeditationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val _typeItem: Int = 0
    private val _typeLoading: Int = 1

    var isLoading: Boolean = false

    var meditations: List<BaseContent?> = mutableListOf()

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.meditation_title)
        var date: TextView = view.findViewById(R.id.meditation_date)
        var note: ImageView = view.findViewById(R.id.meditation_note)
    }

    class LoadingViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == _typeItem) {
            return ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_meditation, parent, false)
            )
        }
        return LoadingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.loading_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val meditation = meditations[position]!!

            holder.title.text = meditation.title

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.CANADA)
            val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
            holder.date.text = newFormat.format(dateFormat.parse(meditation.dateCreated!!)!!)

            val db = NoteDatabase.getDatabase(context = holder.itemView.context)
            db.noteDao().loadByContentId(meditation.id!!).observe(
                holder.itemView.context as LifecycleOwner,
                { note ->
                    val i = Intent(holder.itemView.context, DetailActivity::class.java)
                    i.putExtra("data", Gson().toJson(meditations[position]))

                    if (note != null) {
                        holder.note.visibility = View.VISIBLE
                        i.putExtra("noteId", note.id)
                    } else {
                        holder.note.visibility = View.GONE
                    }

                    holder.itemView.setOnClickListener {
                        if (meditation.files.isNullOrEmpty()) {
                            holder.itemView.context.startActivity(i)
                        } else {
                            val pdfIntent =
                            Intent(holder.itemView.context, BasicWebviewActivity::class.java)
                            pdfIntent.putExtra("url",
                                WebUrls.PDF_BASE.format(meditation.files!![0]))
                            pdfIntent.putExtra("strTitle", meditation.title)
                            holder.itemView.context.startActivity(pdfIntent)
                        }
                    }
                }
            )
        }
    }

    override fun getItemCount(): Int {
        return meditations.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (meditations[position] == null) _typeLoading else _typeItem
    }
}