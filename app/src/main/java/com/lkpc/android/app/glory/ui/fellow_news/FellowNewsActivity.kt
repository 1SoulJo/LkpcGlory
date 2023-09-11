package com.lkpc.android.app.glory.ui.fellow_news

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
import com.lkpc.android.app.glory.databinding.ActivityFellowNewsBinding
import com.lkpc.android.app.glory.entity.BaseContent

class FellowNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFellowNewsBinding

    private var actionBarTitle: TextView? = null
    private var actionBarBackBtn: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFellowNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.action_bar)

        actionBarTitle = supportActionBar?.customView?.findViewById(R.id.ab_title)
        actionBarBackBtn = supportActionBar?.customView?.findViewById(R.id.ab_btn_back)

        actionBarTitle?.text = getString(R.string.fellow_news)

        actionBarBackBtn?.visibility = View.VISIBLE
        actionBarBackBtn?.setOnClickListener {
            finish()
        }

        binding.rvFellowNews.layoutManager = LinearLayoutManager(this)
        binding.rvFellowNews.adapter = FellowNewsAdapter()

        // data observation
        val viewModel : FellowNewsViewModel by viewModels()

        val observer = Observer<List<BaseContent?>> { data ->
            if (binding.rvFellowNews != null) {
                val adapter = binding.rvFellowNews.adapter as FellowNewsAdapter
                if (adapter.isLoading) {
                    (adapter.fellowNews as MutableList<BaseContent?>)
                        .removeAt(adapter.fellowNews.size - 1)
                    adapter.isLoading = false
                }
                adapter.fellowNews = data
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.getData().observe(this, observer)

        // setup refresh
        binding.fellowNewsLayout.setOnRefreshListener {
            viewModel.addData(0)
            binding.fellowNewsLayout.isRefreshing = false
        }

        // scroll listener
        binding.rvFellowNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val adapter = binding.rvFellowNews.adapter as FellowNewsAdapter
                if (!binding.rvFellowNews.canScrollVertically(1) && !adapter.isLoading) {
                    (adapter.fellowNews as MutableList).add(null)
                    adapter.notifyItemInserted(adapter.fellowNews.size - 1)
                    binding.rvFellowNews.scrollToPosition(adapter.fellowNews.size - 1)

                    viewModel.addData(adapter.itemCount - 1)

                    adapter.isLoading = true
                }
            }
        })
    }
}