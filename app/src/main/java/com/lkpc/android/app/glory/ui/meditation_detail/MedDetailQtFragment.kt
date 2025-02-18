package com.lkpc.android.app.glory.ui.meditation_detail

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.data.QtDatabase
import com.lkpc.android.app.glory.databinding.FragmentMedDetailPrayerBinding
import com.lkpc.android.app.glory.databinding.FragmentMedDetailQtBinding
import com.lkpc.android.app.glory.entity.MeditationV2
import com.lkpc.android.app.glory.entity.Qt
import com.lkpc.android.app.glory.ui.meditation.MeditationViewModelV2
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
    ): View {
        _binding = FragmentMedDetailQtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var model: MeditationV2? = null
        val refTexts = listOf(binding.qtRefText1, binding.qtRefText2, binding.qtRefText3, binding.qtRefText4, binding.qtRefText5)
        val refEdits = listOf(binding.qtRefEdit1, binding.qtRefEdit2, binding.qtRefEdit3, binding.qtRefEdit4, binding.qtRefEdit5)
        val appTexts = listOf(binding.qtAppText1, binding.qtAppText2, binding.qtAppText3, binding.qtAppText4, binding.qtAppText5)
        val appEdits = listOf(binding.qtAppEdit1, binding.qtAppEdit2, binding.qtAppEdit3, binding.qtAppEdit4, binding.qtAppEdit5)
        val viewModel: MeditationViewModelV2 by activityViewModels()
        viewModel.currentModel.observe(viewLifecycleOwner) { it ->
            model = it
            refEdits.forEach {
                it.text.clear()
            }
            appEdits.forEach {
                it.text.clear()
            }
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

        viewModel.currentTextSize.observe(viewLifecycleOwner) { size ->
            refTexts.forEach { it.setTextSize(TypedValue.COMPLEX_UNIT_SP, size) }
            appTexts.forEach { it.setTextSize(TypedValue.COMPLEX_UNIT_SP, size) }
        }

        binding.qtSaveBtn.setOnClickListener {
            val context = context ?: return@setOnClickListener
            val qt = Qt(
                contentId = model?.id?.id,
                title = model?.title,
                refText1 = if (refTexts[0].text?.isNotEmpty() == true) refTexts[0].text?.toString() else null,
                refText2 = if (refTexts[1].text?.isNotEmpty() == true) refTexts[1].text?.toString() else null,
                refText3 = if (refTexts[2].text?.isNotEmpty() == true) refTexts[2].text?.toString() else null,
                refText4 = if (refTexts[3].text?.isNotEmpty() == true) refTexts[3].text?.toString() else null,
                refText5 = if (refTexts[4].text?.isNotEmpty() == true) refTexts[4].text?.toString() else null,
                refEdit1 = if (refEdits[0].text?.isNotEmpty() == true) refEdits[0].text?.toString() else null,
                refEdit2 = if (refEdits[1].text?.isNotEmpty() == true) refEdits[1].text?.toString() else null,
                refEdit3 = if (refEdits[2].text?.isNotEmpty() == true) refEdits[2].text?.toString() else null,
                refEdit4 = if (refEdits[3].text?.isNotEmpty() == true) refEdits[3].text?.toString() else null,
                refEdit5 = if (refEdits[4].text?.isNotEmpty() == true) refEdits[4].text?.toString() else null,
                appText1 = if (appTexts[0].text?.isNotEmpty() == true) appTexts[0].text?.toString() else null,
                appText2 = if (appTexts[1].text?.isNotEmpty() == true) appTexts[1].text?.toString() else null,
                appText3 = if (appTexts[2].text?.isNotEmpty() == true) appTexts[2].text?.toString() else null,
                appText4 = if (appTexts[3].text?.isNotEmpty() == true) appTexts[3].text?.toString() else null,
                appText5 = if (appTexts[4].text?.isNotEmpty() == true) appTexts[4].text?.toString() else null,
                appEdit1 = if (appEdits[0].text?.isNotEmpty() == true) appEdits[0].text?.toString() else null,
                appEdit2 = if (appEdits[1].text?.isNotEmpty() == true) appEdits[1].text?.toString() else null,
                appEdit3 = if (appEdits[2].text?.isNotEmpty() == true) appEdits[2].text?.toString() else null,
                appEdit4 = if (appEdits[3].text?.isNotEmpty() == true) appEdits[3].text?.toString() else null,
                appEdit5 = if (appEdits[4].text?.isNotEmpty() == true) appEdits[4].text?.toString() else null,
            )
            val dao = QtDatabase.getDatabase(context).qtDao()
            GlobalScope.launch {
                dao.insert(qt)
            }
        }
    }
}