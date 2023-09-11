package com.lkpc.android.app.glory.ui.calendar

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.CalendarFragmentBinding

class CalendarActivity : AppCompatActivity() {
    private lateinit var binding: CalendarFragmentBinding

    private var actionBarTitle: TextView? = null
    private var actionBarBackBtn: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CalendarFragmentBinding.inflate(layoutInflater)
        setContentView(R.layout.calendar_fragment)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.action_bar)

        actionBarTitle = supportActionBar?.customView?.findViewById(R.id.ab_title)
        actionBarBackBtn = supportActionBar?.customView?.findViewById(R.id.ab_btn_back)

        actionBarTitle?.text = getString(R.string.church_events)

        actionBarBackBtn?.visibility = View.VISIBLE
        actionBarBackBtn?.setOnClickListener{
            finish()
        }

        val viewModel : CalendarViewModel by viewModels()

        binding.rvCalendar.layoutManager = LinearLayoutManager(this)
        binding.rvCalendar.adapter = viewModel.adapter
        viewModel.getData().observe(this as LifecycleOwner, { events ->
            (binding.rvCalendar.adapter as CalendarAdapter).items = events
            (binding.rvCalendar.adapter as CalendarAdapter).notifyDataSetChanged()
        })
    }
}