package com.lkpc.android.app.glory.ui.cell_church

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
import com.lkpc.android.app.glory.databinding.ActivityCellChurchBinding
import com.lkpc.android.app.glory.entity.BaseContent


class CellChurchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCellChurchBinding

    private var actionBarTitle: TextView? = null
    private var actionBarBackBtn: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCellChurchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.action_bar)

        actionBarTitle = supportActionBar?.customView?.findViewById(R.id.ab_title)
        actionBarBackBtn = supportActionBar?.customView?.findViewById(R.id.ab_btn_back)

        actionBarTitle?.text = getString(R.string.cell_church)

        actionBarBackBtn?.visibility = View.VISIBLE
        actionBarBackBtn?.setOnClickListener {
            finish()
        }

        binding.rvCellChurch.layoutManager = LinearLayoutManager(this)
        binding.rvCellChurch.adapter = CellChurchAdapter()

        // data observation
        val viewModel : CellChurchViewModel by viewModels()

        val observer = Observer<List<BaseContent?>> { data ->
            val adapter = binding.rvCellChurch.adapter as CellChurchAdapter
            if (adapter.isLoading) {
                (adapter.cellChurchListItems as MutableList<BaseContent?>)
                    .removeAt(adapter.cellChurchListItems.size - 1)
                adapter.isLoading = false
            }
            adapter.cellChurchListItems = data
            adapter.notifyDataSetChanged()
        }
        viewModel.getData().observe(this, observer)

        // setup refresh
        binding.cellChurchLayout.setOnRefreshListener {
            viewModel.addData(0)
            binding.cellChurchLayout.isRefreshing = false
        }

        // scroll listener
        binding.rvCellChurch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val adapter = binding.rvCellChurch.adapter as CellChurchAdapter
                if (!binding.rvCellChurch.canScrollVertically(1) && !adapter.isLoading) {
                    (adapter.cellChurchListItems as MutableList).add(null)
                    adapter.notifyItemInserted(adapter.cellChurchListItems.size - 1)
                    binding.rvCellChurch.scrollToPosition(adapter.cellChurchListItems.size - 1)

                    viewModel.addData(adapter.itemCount - 1)

                    adapter.isLoading = true
                }
            }
        })
    }
}
