package com.lkpc.android.app.glory.ui.meditation_detail

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.FragmentMedDetailMainBinding
import com.lkpc.android.app.glory.entity.MeditationV2
import com.lkpc.android.app.glory.ui.meditation.MeditationViewModelV2
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener

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
            android.R.layout.simple_dropdown_item_1line,
            listOf("개역개정", "새번역", "ESV"))

        binding.bibleSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!this@MedDetailMainFragment::dataModel.isInitialized) {
                    return
                }
                when (position) {
                    0 -> {
                        binding.bibleMain.text = dataModel.bible1?.replace("\n", "\n\n")
                    }
                    1 -> {
                        binding.bibleMain.text = dataModel.bible2?.replace("\n", "\n\n")
                    }
                    else -> {
                        binding.bibleMain.text = dataModel.bible3?.replace("\n", "\n\n")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        val viewModel: MeditationViewModelV2 by activityViewModels()
        viewModel.currentModel.observe(viewLifecycleOwner) {
            dataModel = it ?: return@observe

            binding.medDetailMain.scrollTo(0, 0)

            val selectedBible = binding.bibleSpinner.selectedItemPosition
            binding.bibleMain.text = when (selectedBible) {
                0 -> dataModel.bible1?.replace("\n", "\n\n")
                1 -> dataModel.bible2?.replace("\n", "\n\n")
                else -> dataModel.bible3?.replace("\n", "\n\n")
            }

            binding.videoLoadingView.visibility = View.GONE
            binding.youtubeVideoLayout.visibility = View.GONE
        }

        viewModel.currentTextSize.observe(viewLifecycleOwner) { size ->
            binding.bibleMain.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
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