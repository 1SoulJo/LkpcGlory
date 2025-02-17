package com.lkpc.android.app.glory

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessaging
import com.lkpc.android.app.glory.constants.Notification
import com.lkpc.android.app.glory.constants.Notification.Companion.CHANNEL_ID
import com.lkpc.android.app.glory.constants.SharedPreference
import com.lkpc.android.app.glory.constants.WebUrls
import com.lkpc.android.app.glory.databinding.ActivityMainBinding
import com.lkpc.android.app.glory.ui.basic_webview.BasicWebviewActivity
import com.lkpc.android.app.glory.ui.bulletin.BulletinActivity
import com.lkpc.android.app.glory.ui.calendar.CalendarActivity
import com.lkpc.android.app.glory.ui.cell_church.CellChurchActivity
import com.lkpc.android.app.glory.ui.column.ColumnFragment
import com.lkpc.android.app.glory.ui.detail.DetailActivity
import com.lkpc.android.app.glory.ui.fellow_news.FellowNewsActivity
import com.lkpc.android.app.glory.ui.fellow_news.FellowNewsFragment
import com.lkpc.android.app.glory.ui.home.HomeFragment
import com.lkpc.android.app.glory.ui.location.LocationActivity
import com.lkpc.android.app.glory.ui.meditation.MeditationFragment
import com.lkpc.android.app.glory.ui.meditation.MeditationViewModelV2
import com.lkpc.android.app.glory.ui.meditation_detail.MeditationDetailFragment
import com.lkpc.android.app.glory.ui.news.NewsFragment
import com.lkpc.android.app.glory.ui.note.NoteListActivity
import com.lkpc.android.app.glory.ui.sermon.SermonFragment
import com.lkpc.android.app.glory.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    companion object {
        const val CURRENT_FRAGMENT = "current_fragment"
        const val TAG_HOME = "home"
        const val TAG_COLUMN = "column"
        const val TAG_SERMON = "sermon"
        const val TAG_MEDITATION = "meditation"
        const val TAG_MEDITATION_DETAIL = "meditation_detail"
        const val TAG_NEWS = "news"
        const val TAG_FELLOW_NEWS = "fellow_news"
    }

    private val homeFragment : HomeFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_HOME)
        if (fr != null) {
            fr as HomeFragment
        } else {
            HomeFragment()
        }
    }

    private val columnFragment : ColumnFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_COLUMN)
        if (fr != null) {
            fr as ColumnFragment
        } else {
            ColumnFragment()
        }
    }

    private val sermonFragment : SermonFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_SERMON)
        if (fr != null) {
            fr as SermonFragment
        } else {
            SermonFragment()
        }
    }

//    private val meditationFragment : MeditationFragment by lazy {
//        val fr = supportFragmentManager.findFragmentByTag(TAG_MEDITATION)
//        if (fr != null) {
//            fr as MeditationFragment
//        } else {
//            MeditationFragment()
//        }
//    }

    private val meditationDetailFragment : MeditationDetailFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_MEDITATION_DETAIL)
        if (fr != null) {
            fr as MeditationDetailFragment
        } else {
            MeditationDetailFragment()
        }
    }

    private val newsFragment : NewsFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_NEWS)
        if (fr != null) {
            fr as NewsFragment
        } else {
            NewsFragment()
        }
    }

    private val fellowNewsFragment : FellowNewsFragment by lazy {
        val fr = supportFragmentManager.findFragmentByTag(TAG_FELLOW_NEWS)
        if (fr != null) {
            fr as FellowNewsFragment
        } else {
            FellowNewsFragment()
        }
    }

    private var selectedFragment : Int = R.id.navigation_home
    private var activeFragment  : Fragment? = null

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        binding.appBarMain.toolBar.toolbarTitle.setText(R.string.lpc)

        // handle bundle data
        val data = intent.getStringExtra("contents")
        if (data != null) {
            // this means activity started from notification while app was in background state
            val i = Intent(this, DetailActivity::class.java)
            i.putExtra("singleContentId", data)
            startActivity(i)
        }

        // retrieve current fragment from savedInstanceState
        savedInstanceState?.let {
            selectedFragment = it.getInt(CURRENT_FRAGMENT, R.id.navigation_home)
        }

        when (selectedFragment) {
            R.id.navigation_home -> activeFragment = homeFragment
            R.id.navigation_column -> activeFragment = columnFragment
            R.id.navigation_sermon -> activeFragment = sermonFragment
            R.id.navigation_meditation -> activeFragment = meditationDetailFragment
//            R.id.navigation_meditation -> activeFragment = meditationFragment
            R.id.navigation_fellow_news -> activeFragment = fellowNewsFragment
        }

        if (savedInstanceState == null) {
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.let {
                supportFragmentManager.beginTransaction().detach(it).commitNow()
            }
            // add all fragments but show only active fragment
            supportFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment, homeFragment, TAG_HOME).hide(homeFragment)
                .add(R.id.nav_host_fragment, columnFragment, TAG_COLUMN).hide(columnFragment)
                .add(R.id.nav_host_fragment, sermonFragment, TAG_SERMON).hide(sermonFragment)
//                .add(R.id.nav_host_fragment, meditationFragment , TAG_MEDITATION).hide(meditationFragment)
                .add(R.id.nav_host_fragment, meditationDetailFragment , TAG_MEDITATION_DETAIL).hide(meditationDetailFragment)
                .add(R.id.nav_host_fragment, fellowNewsFragment , TAG_FELLOW_NEWS).hide(fellowNewsFragment)
                .show(activeFragment!!)
                .commit()
        }
        setupNavigationView()
        setupNotification()

        binding.appBarMain.toolBar.toolbarTitle.setTextColor(Color.parseColor("#666566"))

        binding.appBarMain.toolBar.toolbarFontBtn.setOnClickListener {
            val viewModel: MeditationViewModelV2 by viewModels()
            viewModel.onTextSizeButtonClicked()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isOpen) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setupNavigationView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // setup drawer
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_column, R.id.navigation_sermon,
            R.id.navigation_meditation, R.id.navigation_fellow_news,
            R.id.nav_menu_my_note, R.id.nav_menu_online_meet,
            R.id.nav_menu_church_events, R.id.nav_menu_service_info,
            R.id.nav_menu_nav_guide), binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.drawerNavView.setupWithNavController(navController)
        binding.drawerNavView.setNavigationItemSelectedListener { item ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            when (item.itemId) {
                R.id.nav_menu_my_note -> {
                    startActivity(Intent(this, NoteListActivity::class.java))
                    true
                }
                R.id.nav_menu_online_meet -> {
                    val i = Intent(this, BasicWebviewActivity::class.java)
                    i.putExtra("title", R.string.online_meet)
                    i.putExtra("url", WebUrls.ONLINE_MEET_REG)
                    startActivity(i)
                    true
                }
                R.id.nav_menu_cell_church -> {
                    val i = Intent(this, CellChurchActivity::class.java)
                    startActivity(i)
                    true
                }
                R.id.nav_menu_fellow_news -> {
                    val i = Intent(this, FellowNewsActivity::class.java)
                    startActivity(i)
                    true
                }
                R.id.nav_menu_church_events -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    true
                }

                R.id.nav_menu_downtown -> {
                    val i = Intent(this, BulletinActivity::class.java)
                    i.putExtra("isDowntown", true)
                    startActivity(i)
                    true
                }
                R.id.nav_menu_service_info -> {
                    val i = Intent(this, BasicWebviewActivity::class.java)
                    i.putExtra("type", BasicWebviewActivity.TYPE_SERVICE_INFO)
                    i.putExtra("title", R.string.service_info)
                    startActivity(i)
                    true
                }
                R.id.nav_menu_homepage -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WebUrls.LPC_HOMEPAGE)))
                    true
                }
                R.id.nav_menu_nav_guide -> {
                    val i = Intent(this, LocationActivity::class.java)
                    startActivity(i)
                    true
                }
                R.id.nav_menu_settings -> {
                    val i = Intent(this, SettingsActivity::class.java)
                    startActivity(i)
                    true
                }
                else -> true
            }
        }

        // setup bottom navigation
        binding.appBarMain.bottomNavView.setupWithNavController(navController)
        binding.appBarMain.bottomNavView.setOnItemSelectedListener {
            setFragment(it.itemId)
        }
    }

    private fun setupNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val general = sp.getBoolean(SharedPreference.NOTIFICATION_TOPIC_GENERAL, true)
        if (general) {
            FirebaseMessaging.getInstance().subscribeToTopic(Notification.TOPIC_GENERAL)
        }
        val urgent = sp.getBoolean(SharedPreference.NOTIFICATION_TOPIC_URGENT, true)
        if (urgent) {
            FirebaseMessaging.getInstance().subscribeToTopic(Notification.TOPIC_URGENT)
        }
    }

    private fun setFragment(itemId: Int): Boolean {
        selectedFragment = itemId
        supportFragmentManager.beginTransaction().hide(meditationDetailFragment).commitNow()
        resetToolbar()
        when (itemId){
            R.id.navigation_home -> {
                if (activeFragment is HomeFragment) {
                    return false
                }
                supportFragmentManager.beginTransaction().hide(activeFragment!!)
                    .show(homeFragment)
                    .commit()
                activeFragment = homeFragment
                binding.appBarMain.toolBar.toolbarTitle.setText(R.string.lpc)
            }
            R.id.navigation_column  ->{
                if (activeFragment is ColumnFragment) {
                    return false
                }
                supportFragmentManager.beginTransaction().hide(activeFragment!!)
                    .show(columnFragment)
                    .commit()
                activeFragment = columnFragment
                binding.appBarMain.toolBar.toolbarTitle.setText(R.string.title_column)
            }
            R.id.navigation_sermon ->{
                if (activeFragment is SermonFragment) {
                    return false
                }
                supportFragmentManager.beginTransaction().hide(activeFragment!!)
                    .show(sermonFragment)
                    .commit()
                activeFragment = sermonFragment
                binding.appBarMain.toolBar.toolbarTitle.setText(R.string.title_sermon)
            }
            R.id.navigation_meditation ->{
                supportFragmentManager.beginTransaction().hide(activeFragment!!)
                    .show(meditationDetailFragment)
                    .commit()
                activeFragment = meditationDetailFragment
                binding.appBarMain.toolBar.toolbarTitle.setText(R.string.title_meditation)
                binding.appBarMain.toolBar.toolbarFontBtn.visibility = View.VISIBLE
            }
            R.id.navigation_fellow_news ->{
                if (activeFragment is FellowNewsFragment) {
                    return false
                }
                supportFragmentManager.beginTransaction().hide(activeFragment!!)
                    .show(fellowNewsFragment).commit()
                activeFragment = fellowNewsFragment
                binding.appBarMain.toolBar.toolbarTitle.setText(R.string.fellow_news)
            }
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_FRAGMENT, selectedFragment)
    }

    private fun resetToolbar() {
        binding.appBarMain.toolBar.toolbarFontBtn.visibility = View.GONE
    }
}
