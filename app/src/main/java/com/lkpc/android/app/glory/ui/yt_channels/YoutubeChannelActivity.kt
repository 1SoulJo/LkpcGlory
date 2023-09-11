package com.lkpc.android.app.glory.ui.yt_channels

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.constants.WebUrls
import com.lkpc.android.app.glory.databinding.ActivityYoutubeChannelBinding

class YoutubeChannelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityYoutubeChannelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubeChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.action_bar)

        // setup action bar
        findViewById<TextView>(R.id.ab_title).text = getString(R.string.lpc_youtube_channels_kr)
        findViewById<ImageView>(R.id.ab_btn_back).visibility = View.VISIBLE
        findViewById<ImageView>(R.id.ab_btn_back).setOnClickListener {
            finish()
        }

        // setup buttons
        binding.channel1.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WebUrls.LKPC_CHANNEL)))
        }
        binding.channel2.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WebUrls.YA_CHANNEL)))
        }
        binding.channel3.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WebUrls.EC_CHANNEL)))
        }
        binding.channel4.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WebUrls.NHF_CHANNEL)))
        }
        binding.channel5.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WebUrls.DT_CHANNEL)))
        }
    }
}