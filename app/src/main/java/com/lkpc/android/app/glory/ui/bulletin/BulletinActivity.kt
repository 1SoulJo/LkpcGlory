package com.lkpc.android.app.glory.ui.bulletin

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
import com.lkpc.android.app.glory.databinding.ActivityBulletinBinding
import com.lkpc.android.app.glory.entity.BaseContent

class BulletinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBulletinBinding
    private var actionBarTitle: TextView? = null
    private var actionBarBackBtn: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBulletinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.action_bar)
        actionBarTitle = supportActionBar?.customView?.findViewById(R.id.ab_title)
        actionBarBackBtn = supportActionBar?.customView?.findViewById(R.id.ab_btn_back)

        val isDowntown = intent.getBooleanExtra("isDowntown", false)

        if (isDowntown) {
            actionBarTitle?.setText(R.string.downtown_bulletin_kr)
        } else {
            actionBarTitle?.setText(R.string.bulletin_kr)
        }
        actionBarBackBtn?.visibility = View.VISIBLE
        actionBarBackBtn?.setOnClickListener {
            finish()
        }

        binding.rvBulletin.layoutManager = LinearLayoutManager(this)
        binding.rvBulletin.adapter = BulletinAdapter()

        // data observation
        val viewModel : BulletinViewModel by viewModels { BulletinViewModelFactory(isDowntown) }

        val observer = Observer<List<BaseContent?>> { data ->
            if (data.isNullOrEmpty()) {
                binding.bulletinEmptyText.visibility = View.VISIBLE
                return@Observer
            }
            if (binding.rvBulletin != null) {
                val adapter = binding.rvBulletin.adapter as BulletinAdapter
                if (adapter.isLoading) {
                    (adapter.bulletins as MutableList<BaseContent?>).removeAt(adapter.bulletins.size - 1)
                    adapter.isLoading = false
                }
                adapter.bulletins = data
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.getData().observe(this, observer)

        // setup refresh
        binding.bulletinLayout.setOnRefreshListener {
            viewModel.addData(0)
            binding.bulletinLayout.isRefreshing = false
        }

        // scroll listener
        binding.rvBulletin.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val adapter = binding.rvBulletin.adapter as BulletinAdapter
                if (!binding.rvBulletin.canScrollVertically(1) && !adapter.isLoading) {
                    (adapter.bulletins as MutableList).add(null)
                    adapter.notifyItemInserted(adapter.bulletins.size - 1)
                    binding.rvBulletin.scrollToPosition(adapter.bulletins.size - 1)

                    viewModel.addData(adapter.itemCount - 1)

                    adapter.isLoading = true
                }
            }
        })
    }
}