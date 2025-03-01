package com.lkpc.android.app.glory.ui.detail

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.ui.PlayerControlView
//import androidx.media3.ui.PlayerNotificationManager
import androidx.media3.ui.PlayerNotificationManager.BitmapCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX
import com.google.gson.Gson
import com.lkpc.android.app.glory.BuildConfig
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.api_client.ContentApiClient
import com.lkpc.android.app.glory.constants.ContentType
import com.lkpc.android.app.glory.constants.Notification.Companion.CHANNEL_ID
import com.lkpc.android.app.glory.constants.Notification.Companion.SERMON_AUDIO_ID
import com.lkpc.android.app.glory.constants.WebUrls.Companion.SERMON_AUDIO_SRC
import com.lkpc.android.app.glory.databinding.ActivityDetailBinding
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.ui.note.NoteDetailActivity
import com.lkpc.android.app.glory.ui.note.NoteEditActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class DetailActivity : AppCompatActivity() {
    private val _editNodeActivityResultCode = 1
//    private lateinit var playerNotificationManager: PlayerNotificationManager

    private var noteId : Int = -1
    private lateinit var content : BaseContent

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        noteId = intent.getIntExtra("noteId", -1)

        val contentId = intent.getStringExtra("singleContentId")
        if (contentId.isNullOrEmpty()) {
            val data = intent.getStringExtra("data")
            content = Gson().fromJson(data, BaseContent::class.java)

            // fill main content area
            fillContent(content)
            invalidateOptionsMenu()

            ContentApiClient().increaseViewCount(content.id!!)
        } else {
            ContentApiClient().loadSingleContent(contentId, object: Callback<BaseContent> {
                override fun onResponse(call: Call<BaseContent>, response: Response<BaseContent>) {
                    content = response.body()!!
                    fillContent(content)
                    invalidateOptionsMenu()

                    ContentApiClient().increaseViewCount(content.id!!)
                }

                override fun onFailure(call: Call<BaseContent>, t: Throwable) { }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // returning from NoteEditActivity
        if (requestCode == _editNodeActivityResultCode) {
            noteId = resultCode
            invalidateOptionsMenu()
        }
    }

    override fun onPause() {
        super.onPause()
        supportFragmentManager.findFragmentById(R.id.detail_youtube_fragment)?.onPause()
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onDestroy() {
        super.onDestroy()

//        val detailAudio = binding.detailAudio
//        if (detailAudio.player != null) {
////            playerNotificationManager.setPlayer(null)
//            detailAudio.player?.release()
//            detailAudio.player = null
//        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val newNoteMenu = menu!!.findItem(R.id.detail_menu_new_note)
        val openNoteMenu = menu.findItem(R.id.detail_menu_open_note)

        if (noteId > -1) {
            openNoteMenu.isVisible = true
            newNoteMenu.isVisible = false
        } else {
            openNoteMenu.isVisible = false
            newNoteMenu.isVisible = true
        }

        if (this::content.isInitialized) {
            if (content.category == ContentType.SERMON
                || content.category == ContentType.CELL_CHURCH) {
                menu.findItem(R.id.detail_menu_share).isVisible = false
            }

            if (content.category == ContentType.NEWS
                || content.category == ContentType.CELL_CHURCH
                || content.category == ContentType.FELLOW_NEWS) {
                newNoteMenu.isVisible = false
                openNoteMenu.isVisible = false
            }
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.content_detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.detail_menu_new_note -> {
                val i = Intent(this, NoteEditActivity::class.java)
                i.putExtra("type", content.category)
                i.putExtra("title", content.title)
                i.putExtra("contentId", content.id)
                startActivityForResult(i, _editNodeActivityResultCode)
            }

            R.id.detail_menu_open_note -> {
                val i = Intent(this, NoteDetailActivity::class.java)
                i.putExtra("noteId", noteId)
                startActivity(i)
            }

            R.id.detail_menu_share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${content.title} \n ${binding.contentBody.text}")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun fillContent(content: BaseContent) {
        // title
        val tbTitle = findViewById<TextView>(R.id.toolbar_title)
        tbTitle?.text = content.title

        // file area
        if (content.files?.isNotEmpty() == true) {
            binding.rvFiles.layoutManager = LinearLayoutManager(this)
            binding.rvFiles.adapter = FileAdapter(content.files!!)
        }

        // content title
        binding.contentTitle.text = content.title

        // chapter
        if (content.category == ContentType.SERMON) {
            binding.contentChapter.visibility = View.VISIBLE
            binding.contentChapter.text = content.chapter
        }

        // date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.CANADA)
        val newFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.CANADA)
        binding.contentDate.text = newFormat.format(dateFormat.parse(content.dateCreated!!)!!)

        // setup youtube video is available
        content.videoLink?.let{
            setupYoutubeView(it)
        }

        // setup audio if available
        if (content.audioLink?.isNotEmpty() == true) {
//            playerNotificationManager = PlayerNotificationManager(
//                this, CHANNEL_ID, SERMON_AUDIO_ID,
//                DescriptionAdapter(getString(R.string.title_sermon), content.title!!))

//            val audioPlayer = SimpleExoPlayer.Builder(this).build()
            val audioPlayer = ExoPlayer.Builder(this).build()
            val url = SERMON_AUDIO_SRC.format(content.audioLink)
            val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(url))
            audioPlayer.setMediaItem(mediaItem)
            audioPlayer.prepare()
//            audioPlayer.addListener(object : Player.EventListener {
//                override fun onIsPlayingChanged(isPlaying: Boolean) {
//                    super.onIsPlayingChanged(isPlaying)
//                    if (isPlaying) {
//                        playerNotificationManager.setPlayer(audioPlayer)
//                    }
//                }
//            })

            val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                .build()
            audioPlayer.setAudioAttributes(audioAttributes, true)

//            binding.detailAudio.showTimeoutMs = -1
//            binding.detailAudio.player = audioPlayer

//            binding.btnAudio.visibility = View.VISIBLE
        }

        // remove video and audio file
        val safelist = Safelist()
        safelist.addTags("b", "em", "div", "p", "h1", "h2", "strong", "ol", "li", "ul", "u", "br")
        val doc = Jsoup.parse(content.boardContent)
        val newDoc = Jsoup.clean(doc.toString(), safelist)

        // content body
        if (content.category == ContentType.SERMON) {
            // setup video/audio buttons
            binding.buttonsArea.visibility = View.VISIBLE
            binding.btnVideo.setOnClickListener {
                binding.detailYoutubeView.visibility = View.VISIBLE
                binding.detailYoutubeLayout.visibility = View.GONE
//                (binding.detailAudio as PlayerControlView).hide()
//                if (binding.detailAudio.player != null) {
//                    (binding.detailAudio.player as ExoPlayer).pause()
//                }
            }
//            binding.btnAudio.setOnClickListener {
//                binding.detailYoutubeLayout.visibility = View.GONE
//                (binding.detailAudio as PlayerControlView).show()
//            }
        }

        binding.contentBody.text =
            HtmlCompat.fromHtml(newDoc, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    private fun setupYoutubeView(id: String) {
        val playerView = binding.youtubePlayerView
        playerView.getYouTubePlayerWhenReady(object: YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                youTubePlayer.cueVideo(id, 0F)
            }
        })

        val fragment = supportFragmentManager.findFragmentById(R.id.detail_youtube_fragment)
        val yf = fragment as YouTubePlayerSupportFragmentX
        yf.initialize(
            BuildConfig.YOUTUBE_API,
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(provider: YouTubePlayer.Provider,
                                                     youTubePlayer: YouTubePlayer, b: Boolean) {
                    youTubePlayer.cueVideo(id)
                    binding.btnVideo.visibility = View.VISIBLE
                }

                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider,
                    youTubeInitializationResult: YouTubeInitializationResult) {
                    Log.d("DetailActivity", youTubeInitializationResult.toString())
                }
            }
        )
    }

//    private class DescriptionAdapter(val title: String, val contentText: String) :
//        PlayerNotificationManager.MediaDescriptionAdapter {
//
//        override fun getCurrentContentTitle(player: Player): String {
//            return title
//        }
//
//        @Nullable
//        override fun getCurrentContentText(player: Player): String? {
//            return contentText
//        }
//
//        @Nullable
//        override fun getCurrentLargeIcon(
//            player: Player,
//            callback: BitmapCallback
//        ): Bitmap? {
//            return null
//        }
//
//        @Nullable
//        override fun createCurrentContentIntent(player: Player): PendingIntent? {
//            return null
//        }
//    }
}