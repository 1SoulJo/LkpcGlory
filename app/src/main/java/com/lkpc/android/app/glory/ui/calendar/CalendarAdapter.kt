package com.lkpc.android.app.glory.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.entity.Event
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items : List<Event> = mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title : TextView = view.findViewById(R.id.title)
        val desc : TextView = view.findViewById(R.id.description)
        val startTime : TextView = view.findViewById(R.id.start_time)
        val endTime : TextView = view.findViewById(R.id.end_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_cal, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val event = items[position]
            holder.title.text = "${event.summary}"

            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA)
            var newFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CANADA)
            holder.startTime.text = newFormat.format(format.parse(event.startTime!!)!!)

            newFormat = SimpleDateFormat(" - HH:mm", Locale.CANADA)
            holder.endTime.text = newFormat.format(format.parse(event.endTime!!)!!)
            if (!event.description.isNullOrEmpty()) {
                holder.desc.visibility = View.VISIBLE
                holder.desc.text = event.description
            } else {
                holder.desc.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}