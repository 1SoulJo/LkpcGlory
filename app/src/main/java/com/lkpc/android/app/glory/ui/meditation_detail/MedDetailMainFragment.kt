package com.lkpc.android.app.glory.ui.meditation_detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX
import com.lkpc.android.app.glory.BuildConfig
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.FragmentMedDetailMainBinding
import com.lkpc.android.app.glory.databinding.FragmentMeditationDetailBinding
import com.lkpc.android.app.glory.entity.MeditationV2
import com.lkpc.android.app.glory.ui.meditation.MeditationViewModelV2
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener

/**
 * A simple [Fragment] subclass.
 * Use the [MedDetailMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MedDetailMainFragment : Fragment(R.layout.fragment_med_detail_main) {
    private var _binding: FragmentMedDetailMainBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var dataModel: MeditationV2

    private val ytListener: YouTubePlayerListener = object: AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
            dataModel.videoLink?.let {
                youTubePlayer.cueVideo(it, 0F)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedDetailMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bibleSpinner.adapter = ArrayAdapter(view.context,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("개역개정", "새번역", "ESV"))

        binding.bibleSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        binding.bibleMain.text = dataModel.bible1
                    }
                    1 -> {
                        binding.bibleMain.text = dataModel.bible2
                    }
                    else -> {
                        binding.bibleMain.text = dataModel.bible3
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        val viewModel: MeditationViewModelV2 by activityViewModels()
        viewModel.currentModel.observe(viewLifecycleOwner) {
            dataModel = it ?: return@observe
            binding.bibleMain.text = dataModel.bible1

            binding.videoLoadingView.visibility = View.GONE
            binding.youtubeVideoLayout.visibility = View.GONE

//            if (dataModel.videoLink == null) {
//                binding.videoLoadingView.visibility = View.VISIBLE
//            } else {
//                binding.youtubeVideoLayout.visibility = View.VISIBLE
//                setupYoutubeView(dataModel.videoLink)
//            }
        }

        binding.videoBtn.setOnClickListener {
            if (dataModel.videoLink?.isNullOrEmpty() == true) {
                binding.videoLoadingView.visibility = View.VISIBLE
                binding.youtubeVideoLayout.visibility = View.GONE
            } else {
                binding.videoLoadingView.visibility = View.GONE
                binding.youtubeVideoLayout.visibility = View.VISIBLE
                setupYoutubeView(dataModel.videoLink)
            }
        }
    }

    private fun setupYoutubeView(id: String?) {
        val playerView = binding.youtubePlayerView
        playerView.getYouTubePlayerWhenReady(object: YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                id?.let {
                    youTubePlayer.cueVideo(it, 0F)
                }
            }
        })
    }
}