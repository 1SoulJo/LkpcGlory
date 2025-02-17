package com.lkpc.android.app.glory.ui.meditation_detail

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.FragmentMedDetailMainBinding
import com.lkpc.android.app.glory.databinding.FragmentMedDetailPrayerBinding
import com.lkpc.android.app.glory.entity.MeditationV2
import com.lkpc.android.app.glory.ui.meditation.MeditationViewModelV2

/**
 * A simple [Fragment] subclass.
 * Use the [MedDetailPrayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MedDetailPrayerFragment : Fragment(R.layout.fragment_med_detail_prayer) {

    private var _binding: FragmentMedDetailPrayerBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var dataModel: MeditationV2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedDetailPrayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: MeditationViewModelV2 by activityViewModels()
        viewModel.currentModel.observe(viewLifecycleOwner) { it ->
            dataModel = it ?: return@observe
            val builder = StringBuilder()
            dataModel.prayList?.forEachIndexed { index, s ->
                builder.append("${index+1}. ")
                builder.append(s)
                builder.append("\n\n")
            }
            binding.medPrayer.text = builder.toString()
        }

        viewModel.currentTextSize.observe(viewLifecycleOwner) { size ->
            binding.medPrayer.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        }
    }
}