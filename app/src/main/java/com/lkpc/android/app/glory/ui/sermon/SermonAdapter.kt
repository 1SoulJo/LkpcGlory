package com.lkpc.android.app.glory.ui.sermon

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.google.gson.Gson
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.api_client.YoutubeImgClient
import com.lkpc.android.app.glory.data.NoteDatabase
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.ui.detail.DetailActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class SermonAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val _typeItem: Int = 0
    private val _typeLoading: Int = 1
    private val _imageTag = "image_view"

    var isLoading: Boolean = false
    var sermons: List<BaseContent?> = mutableListOf()

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var clLayout: ConstraintLayout = view as ConstraintLayout
        var clTextArea: ConstraintLayout = view.findViewById(R.id.sermon_text_area)
        var tvSermonTitle: TextView = view.findViewById(R.id.sermon_title)
        var tvSermonName: TextView = view.findViewById(R.id.sermon_name)
        var tvSermonDate: TextView = view.findViewById(R.id.sermon_date)
        var ivNote: ImageView = view.findViewById(R.id.sermon_note)
    }

    class LoadingViewHolder(view: View): RecyclerView.ViewHolder(view) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == _typeItem) {
            return ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_sermon, parent, false)
            )
        }
        return LoadingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.loading_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val iv = holder.clLayout.findViewWithTag<ImageView>(_imageTag)
            holder.clLayout.removeView(iv)

            val sermon = sermons[position]!!
            holder.tvSermonTitle.text =
                sermon.title!!.replace(")", ")" + System.getProperty("line.separator")!!)
            holder.tvSermonName.text = sermon.videoLinkAuthor

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.CANADA)
            val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
            holder.tvSermonDate.text = newFormat.format(dateFormat.parse(sermon.dateCreated!!)!!)

            if (sermon.videoLink != null) {
                val client = YoutubeImgClient()
                client.getThumbnail(
                    videoId = sermon.videoLink!!,
                    cb = object: Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                updateThumbnail(holder, response)
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                            t?.printStackTrace()
                        }
                })
            }

            val db = NoteDatabase.getDatabase(context = holder.itemView.context)
            db.noteDao().loadByContentId(sermon.id!!).observe(
                holder.itemView.context as LifecycleOwner,
                { note ->
                    val i = Intent(holder.itemView.context, DetailActivity::class.java)
                    i.putExtra("data", Gson().toJson(sermons[position]))

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
            )
        }
    }

    override fun getItemCount(): Int {
        return sermons.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (sermons[position] == null) _typeLoading else _typeItem
    }

    private fun updateThumbnail(holder: ItemViewHolder, res: Response<ResponseBody>) {
        if (res.body() != null) {
            val bmp = BitmapFactory.decodeStream(res.body()!!.byteStream())
            val thumb = ImageView(holder.clLayout.context)
            thumb.setImageBitmap(bmp)

            thumb.tag = _imageTag
            thumb.scaleType = ImageView.ScaleType.CENTER_CROP
            thumb.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )

            val f = Fade(Fade.IN)
            TransitionManager.beginDelayedTransition(holder.clLayout, f)
            holder.clLayout.addView(thumb)
            holder.clTextArea.bringToFront()
        }
    }
}