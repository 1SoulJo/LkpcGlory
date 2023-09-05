package com.lkpc.android.app.glory.ui.note

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.constants.ContentType
import com.lkpc.android.app.glory.entity.Note
import java.text.SimpleDateFormat
import java.util.*


class NoteAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var notes: List<Note> = mutableListOf()
    var selectedNotes = ArrayList<Int>()

    private var actionMode = false
    private lateinit var itemSelectListener : OnItemSelectListener

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var layout: ConstraintLayout = view.findViewById(R.id.note_list_item_layout)
        var checkBox: CheckBox = view.findViewById(R.id.note_checkbox)
        var icon: ImageView = view.findViewById(R.id.note_icon)
        var title: TextView = view.findViewById(R.id.note_title)
        var lastModified: TextView = view.findViewById(R.id.last_modified)

        fun setCheckBoxVisibility(visible: Boolean) {
            TransitionManager.beginDelayedTransition(layout, Fade(Fade.IN))
            checkBox.visibility = when(visible) {
                true -> View.VISIBLE
                false -> View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_note, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CANADA)
        val lastModified = dateFormat.format(Date(notes[position].lastModified!!))

        if (holder is ViewHolder) {
            holder.icon.setImageResource(getIconId(notes[position].type!!))
            holder.title.text = notes[position].title
            holder.lastModified.text = lastModified
            holder.checkBox.isChecked = false
            holder.checkBox.isClickable = false

            holder.itemView.setOnClickListener {
                if (actionMode) {
                    val id = notes[position].id
                    if (!holder.checkBox.isChecked) {
                        if (!selectedNotes.contains(id)) {
                            selectedNotes.add(notes[position].id)
                        }
                    } else {
                        if (selectedNotes.contains(id)) {
                            selectedNotes.remove(notes[position].id)
                        }
                    }
                    holder.checkBox.isChecked = holder.checkBox.isChecked.not()

                    itemSelectListener.onItemSelected(selectedNotes.size)
                } else {
                    val i = Intent(holder.itemView.context, NoteDetailActivity::class.java)
                    i.putExtra("noteId", notes[position].id)
                    holder.itemView.context.startActivity(i)
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)

        if (holder is ViewHolder) {
            holder.setCheckBoxVisibility(actionMode)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun swapData(newData: List<Note>) {
        notes = newData
        notifyDataSetChanged()
    }

    fun setSelectionMode(isSelectMode: Boolean) {
        actionMode = isSelectMode
        notifyDataSetChanged()
    }

    fun setItemSelectListener(l: OnItemSelectListener?) {
        this.itemSelectListener = l!!
    }

    private fun getIconId(type: String): Int {
        return when (type) {
            ContentType.COLUMN -> R.drawable.ic_column
            ContentType.MEDITATION -> R.drawable.ic_meditation
            ContentType.SERMON -> R.drawable.ic_sermon
            else -> 0
        }
    }

    interface OnItemSelectListener {
        fun onItemSelected(count: Int)
    }
}