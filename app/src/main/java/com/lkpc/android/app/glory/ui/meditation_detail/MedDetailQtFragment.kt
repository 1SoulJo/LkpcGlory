package com.lkpc.android.app.glory.ui.meditation_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.FragmentMedDetailPrayerBinding
import com.lkpc.android.app.glory.databinding.FragmentMedDetailQtBinding
import com.lkpc.android.app.glory.ui.meditation.MeditationViewModelV2

/**
 * A simple [Fragment] subclass.
 * Use the [MedDetailQtFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MedDetailQtFragment : Fragment(R.layout.fragment_med_detail_qt) {
    private var _binding: FragmentMedDetailQtBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMedDetailQtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val refTexts = listOf(binding.qtRefText1, binding.qtRefText2, binding.qtRefText3, binding.qtRefText4, binding.qtRefText5)
        val refEdits = listOf(binding.qtRefEdit1, binding.qtRefEdit2, binding.qtRefEdit3, binding.qtRefEdit4, binding.qtRefEdit5)
        val appTexts = listOf(binding.qtAppText1, binding.qtAppText2, binding.qtAppText3, binding.qtAppText4, binding.qtAppText5)
        val appEdits = listOf(binding.qtAppEdit1, binding.qtAppEdit2, binding.qtAppEdit3, binding.qtAppEdit4, binding.qtAppEdit5)
        val viewModel: MeditationViewModelV2 by activityViewModels()
        viewModel.currentModel.observe(viewLifecycleOwner) { it ->
            it?.reflectionList?.forEachIndexed { i, s ->
                refTexts[i].text = "${i + 1}. $s"
                refTexts[i].visibility = View.VISIBLE
                refEdits[i].visibility = View.VISIBLE
            }
            it?.applyList?.forEachIndexed { i, s ->
                appTexts[i].text = "${i + 1}. $s"
                appTexts[i].visibility = View.VISIBLE
                appEdits[i].visibility = View.VISIBLE
            }
        }
    }
}