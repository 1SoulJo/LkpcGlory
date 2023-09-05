package com.lkpc.android.app.glory.ui.location

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.entity.Location


class LocationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var _locations: MutableList<Location> = mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var address: TextView = view.findViewById(R.id.address)
        var tel: TextView = view.findViewById(R.id.tel)
        var fax: TextView = view.findViewById(R.id.fax)
        var email: TextView = view.findViewById(R.id.email)
        var note: TextView = view.findViewById(R.id.note)
        var time: TextView = view.findViewById(R.id.time)
        var img: ImageView = view.findViewById(R.id.img_map)
        var btnMap : LinearLayout = view.findViewById(R.id.map_btn_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_nav_guide, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val location = _locations[position]
            holder.name.text = location.name
            holder.address.text = location.address

            if (location.tel != null) {
                holder.tel.text = location.tel
                holder.tel.visibility = View.VISIBLE
            }

            if (location.fax != null) {
                holder.fax.text = location.fax
                holder.fax.visibility = View.VISIBLE
            }

            if (location.email != null) {
                holder.email.text = location.email
                holder.email.visibility = View.VISIBLE
            }

            if (location.note != null) {
                holder.note.text = location.note
                holder.note.visibility = View.VISIBLE
            }

            if (location.time != null) {
                holder.time.text = location.time
                holder.time.visibility = View.VISIBLE
            }

            holder.btnMap.setOnClickListener {
                val gmmIntentUri: Uri =
                    Uri.parse("google.navigation:q=${location.address}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                holder.itemView.context.startActivity(mapIntent)
            }

            holder.img.setImageResource(location.img)
        }
    }

    override fun getItemCount(): Int {
        return _locations.count()
    }

    fun setLocations(locations: List<Location>) {
        _locations = locations.toMutableList()
    }
}