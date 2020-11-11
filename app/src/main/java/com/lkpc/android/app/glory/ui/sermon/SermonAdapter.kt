package com.lkpc.android.app.glory.ui.sermon

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.google.gson.Gson
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.api_client.YoutubeImgClient
import com.lkpc.android.app.glory.ui.detail.DetailActivity
import kotlinx.android.synthetic.main.list_item_sermon.view.*
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class SermonAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val _typeItem: Int = 0
    private val _typeLoading: Int = 1
    private val _imageTag = "image_view"

    var isLoading: Boolean = false

    var sermons: List<BaseContent?> = mutableListOf()

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var clLayout: ConstraintLayout = view as ConstraintLayout
        var clTextArea: ConstraintLayout = view.sermon_text_area
        var tvSermonTitle: TextView = view.sermon_title
        var tvSermonName: TextView = view.sermon_name
        var tvSermonDate: TextView = view.sermon_date
    }

    class LoadingViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == _typeItem) {
            return ItemViewHolder(
                LayoutInflater.from(context).inflate(R.layout.list_item_sermon, parent, false)
            )
        }
        return LoadingViewHolder(
            LayoutInflater.from(context).inflate(R.layout.loading_row, parent, false)
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

            val doc = Jsoup.parse(sermon.boardContent)
            val frames = doc.getElementsByTag("iframe")
            if (frames.isNotEmpty()) {
                val videoId = frames[0].attr("src").split("/").last()
                val client = YoutubeImgClient()
                client.getThumbnail(videoId = videoId, cb = object: Callback<ResponseBody> {
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

            holder.itemView.setOnClickListener {
                val i = Intent(holder.itemView.context, DetailActivity::class.java)
                i.putExtra("data", Gson().toJson(sermons[position]))
                holder.itemView.context.startActivity(i)
            }
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