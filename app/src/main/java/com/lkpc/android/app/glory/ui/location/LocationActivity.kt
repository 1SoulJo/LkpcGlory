package com.lkpc.android.app.glory.ui.location

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.ActivityLocationBinding
import com.lkpc.android.app.glory.entity.Location
import org.w3c.dom.Text

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding

    private val locations = listOf(
        Location("미시사가", "6965 Professional Court, Mississauga, ON, L4V 1Y3",
        "Tel: 905-677-7729", "Fax: 905-677-7739", "Email: info@lkpc.org",
        "주일 차량 안내를 참고하십시오.", null, R.drawable.location_mississauga),
        Location("다운타운", "455 Huron Street(Bloor St. W / Huron Street), Toronto, ON, M5R 3P2",
        "Tel: 647-436-1977", null, null, null, null, R.drawable.location_downtown)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val toolbarTitle: TextView? = supportActionBar?.customView?.findViewById(R.id.toolbar_title)
        toolbarTitle?.setText(R.string.navigation)

        val adapter = LocationAdapter()
        adapter.setLocations(locations)

        binding.rvLocation.layoutManager = LinearLayoutManager(this)
        binding.rvLocation.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
