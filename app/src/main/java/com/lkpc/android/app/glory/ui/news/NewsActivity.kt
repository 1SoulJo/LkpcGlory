package com.lkpc.android.app.glory.ui.news

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.FragmentNewsBinding
import com.lkpc.android.app.glory.entity.BaseContent

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: FragmentNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar)

        val titleTV: TextView = findViewById(R.id.ab_title)
        val backBtn: ImageView = findViewById(R.id.ab_btn_back)

        // setup action bar
        titleTV.text = getString(R.string.title_notifications)
        backBtn.visibility = View.VISIBLE
        backBtn.setOnClickListener {
            finish()
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.adapter = NewsAdapter()

        // data observation
        val viewModel: NewsViewModel by viewModels()
        val observer = Observer<List<BaseContent?>> { data ->
            if (binding.rvNews != null) {
                val adapter = binding.rvNews.adapter as NewsAdapter
                if (adapter.isLoading) {
                    (adapter.newsList as MutableList<BaseContent?>).removeAt(adapter.newsList.size - 1)
                    adapter.isLoading = false
                }
                adapter.newsList = data
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.getData().observe(this, observer)

        // scroll listener
        binding.rvNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val adapter = binding.rvNews.adapter as NewsAdapter
                if (!binding.rvNews.canScrollVertically(1) && !adapter.isLoading) {
                    (adapter.newsList as MutableList).add(null)
                    adapter.notifyItemInserted(adapter.newsList.size - 1)
                    binding.rvNews.scrollToPosition(adapter.newsList.size - 1)

                    viewModel.addData(adapter.itemCount - 1)

                    adapter.isLoading = true
                }
            }
        })
    }
}