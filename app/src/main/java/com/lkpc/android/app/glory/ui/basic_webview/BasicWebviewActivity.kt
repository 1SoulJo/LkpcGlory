package com.lkpc.android.app.glory.ui.basic_webview

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.api_client.ContentApiClient
import com.lkpc.android.app.glory.databinding.ActivityBasicWebviewBinding
import com.lkpc.android.app.glory.entity.BaseContent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BasicWebviewActivity : AppCompatActivity() {
    companion object {
        const val TYPE_URL = 0
        const val TYPE_SERVICE_INFO = 1
        const val TYPE_NAV_GUIDE = 2
    }

    private val apiCallback = object : Callback<List<BaseContent>> {
        override fun onResponse(
            call: Call<List<BaseContent>>,
            response: Response<List<BaseContent>>
        ) {
            if (response.isSuccessful) {
                val list = response.body() as List<BaseContent>
                var htmlData = list[0].boardContent!!
                htmlData = htmlData.replace('↵', 0.toChar())
                binding.webview.loadDataWithBaseURL(null, htmlData, "text/html", "utf-8", null);
            }
        }

        override fun onFailure(call: Call<List<BaseContent>>, t: Throwable) {
            t.printStackTrace()
        }
    }

    private lateinit var binding: ActivityBasicWebviewBinding
    private var actionBarTitle: TextView? = null
    private var actionBarBackBtn: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.action_bar)
        actionBarTitle = supportActionBar?.customView?.findViewById(R.id.ab_title)
        actionBarBackBtn = supportActionBar?.customView?.findViewById(R.id.ab_btn_back)

        // title
        if (intent.getStringExtra("strTitle") != null) {
            actionBarTitle?.text = intent.getStringExtra("strTitle")
        } else {
            actionBarTitle?.setText(intent.getIntExtra("title", R.string.lpc))
        }

        // back button
        actionBarBackBtn?.visibility = View.VISIBLE
        actionBarBackBtn?.setOnClickListener { finish() }

        // web view setting
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.domStorageEnabled = true
        binding.webview.settings.builtInZoomControls = true
        binding.webview.settings.displayZoomControls = false

        // type
        when(intent.getIntExtra("type", 0)) {
            TYPE_URL -> {
                val url = intent.getStringExtra("url")!!
                binding.webview.loadUrl(url)
            }

            TYPE_SERVICE_INFO -> {
                val apiClient = ContentApiClient()
                apiClient.loadServices(object : Callback<BaseContent> {
                    override fun onResponse(
                        call: Call<BaseContent>,
                        response: Response<BaseContent>
                    ) {
                        if (response.isSuccessful) {
                            val list = response.body() as BaseContent
                            var htmlData = list.boardContent!!
                            htmlData = htmlData.replace('↵', 0.toChar())
                            binding.webview.loadDataWithBaseURL(null, htmlData, "text/html", "utf-8", null);
                        }
                    }

                    override fun onFailure(call: Call<BaseContent>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }

            TYPE_NAV_GUIDE -> {
                val apiClient = ContentApiClient()
                apiClient.loadLocations(apiCallback)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.webview.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.webview.onPause()
    }
}
