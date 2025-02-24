package com.lkpc.android.app.glory.ui.meditation_detail

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.data.QtDatabase
import com.lkpc.android.app.glory.databinding.FragmentMedDetailQtBinding
import com.lkpc.android.app.glory.entity.MeditationV2
import com.lkpc.android.app.glory.entity.Qt
import com.lkpc.android.app.glory.ui.meditation.MeditationViewModelV2
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

        // date change
        viewModel.currentModel.observe(viewLifecycleOwner) { it ->
            binding.qtMainView.scrollTo(0, 0)

            model = it

            refTexts.forEach { it.visibility = View.GONE }
            appTexts.forEach { it.visibility = View.GONE }
            refEdits.forEach {
                it.visibility = View.GONE
                it.text.clear()
            }
            appEdits.forEach {
                it.visibility = View.GONE
                it.text.clear()
            }

            // check if it's Sunday and put questions in 1 section if it's Sunday
            var isSunday = false
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA).parse(it?.scheduledDate ?: "1999-01-01")
            date?.let {
                val cal = Calendar.getInstance(Locale.CANADA)
                cal.time = it
                val dow = cal.get(Calendar.DAY_OF_WEEK)

                if (dow == Calendar.SUNDAY) {
                    isSunday = true
                    binding.qtSection1.text = resources.getString(R.string.qt_section_sunday)
                    binding.qtSection2.visibility = View.GONE
                } else {
                    binding.qtSection1.text = resources.getString(R.string.qt_section_1)
                    binding.qtSection2.text = resources.getString(R.string.qt_section_2)
                    binding.qtSection2.visibility = View.VISIBLE
                }
            }

            var questionId = 1
            it?.reflectionList?.forEachIndexed { i, s ->
                refTexts[i].text = "$questionId. $s"
                refTexts[i].visibility = View.VISIBLE
                refEdits[i].visibility = View.VISIBLE
                questionId += 1
            }
            if (!isSunday) {
                questionId = 1
            }
            it?.applyList?.forEachIndexed { i, s ->
                appTexts[i].text = "$questionId. $s"
                appTexts[i].visibility = View.VISIBLE
                appEdits[i].visibility = View.VISIBLE
                questionId += 1
            }

            context?.let { ctx ->
                val dao = QtDatabase.getDatabase(ctx).qtDao()
                model?.id?.id?.let { contentId ->
                    val qt = dao.loadByContentId(contentId)
                    qt.observe(viewLifecycleOwner) observe2@ { data ->
                        if (data == null) {
                            return@observe2
                        }
                        data.refEdit1?.let { text ->
                            binding.qtRefEdit1.setText(text, TextView.BufferType.EDITABLE)
                        }
                        data.refEdit2?.let { text ->
                            binding.qtRefEdit2.setText(text, TextView.BufferType.EDITABLE)
                        }
                        data.refEdit3?.let { text ->
                            binding.qtRefEdit3.setText(text, TextView.BufferType.EDITABLE)
                        }
                        data.refEdit4?.let { text ->
                            binding.qtRefEdit4.setText(text, TextView.BufferType.EDITABLE)
                        }
                        data.refEdit5?.let { text ->
                            binding.qtRefEdit5.setText(text, TextView.BufferType.EDITABLE)
                        }
                        data.appEdit1?.let { text ->
                            binding.qtAppEdit1.setText(text, TextView.BufferType.EDITABLE)
                        }
                        data.appEdit2?.let { text ->
                            binding.qtAppEdit2.setText(text, TextView.BufferType.EDITABLE)
                        }
                        data.appEdit3?.let { text ->
                            binding.qtAppEdit3.setText(text, TextView.BufferType.EDITABLE)
                        }
                        data.appEdit4?.let { text ->
                            binding.qtAppEdit4.setText(text, TextView.BufferType.EDITABLE)
                        }
                        data.appEdit5?.let { text ->
                            binding.qtAppEdit5.setText(text, TextView.BufferType.EDITABLE)
                        }
                    }
                }
            }
        }

        // text size change
        viewModel.currentTextSize.observe(viewLifecycleOwner) { size ->
            refTexts.forEach { it.setTextSize(TypedValue.COMPLEX_UNIT_SP, size) }
            appTexts.forEach { it.setTextSize(TypedValue.COMPLEX_UNIT_SP, size) }
        }

        // save btn handler
        binding.qtSaveBtn.setOnClickListener {
            val context = context ?: return@setOnClickListener
            model?.id?.id?.let { contentId ->
                val qt = Qt(
                    contentId = contentId,
                    date = model?.scheduledDate,
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
                    val id = dao.insert(qt)
                    if (id > 0) {
                        view.post {
                            Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // share btn handler
        binding.qtShareBtn.setOnClickListener {
            // gen share data
            val builder = StringBuilder()
            binding.qtSection1.text?.let {
                builder.append(it).append("\n")
            }
            refEdits.forEachIndexed { i, edit ->
                val refText = refTexts[i].text
                if (refText.isNotEmpty()) {
                    builder.append(refText).append("\n")
                }

                val editText = edit.text
                if (editText.isNullOrEmpty()) {
                    return@forEachIndexed
                }
                builder.append(" - ").append(editText).append("\n")
            }
            builder.append("\n")
            binding.qtSection2.text?.let {
                builder.append(it).append("\n")
            }
            appEdits.forEachIndexed { i, edit ->
                val appText = appTexts[i].text
                if (appText.isNotEmpty()) {
                    builder.append(appText).append("\n")
                }

                val editText = edit.text
                if (editText.isNullOrEmpty()) {
                    return@forEachIndexed
                }
                builder.append(" - ").append(editText).append("\n")
            }
            val textData = builder.toString()

            val ctx = context ?: return@setOnClickListener
            val alertDialog: AlertDialog = AlertDialog.Builder(ctx, R.style.AlertDialogTheme).create()
            alertDialog.setTitle("묵상 공유")
            alertDialog.setMessage(textData)
            alertDialog.setButton(
                DialogInterface.BUTTON_POSITIVE, "공유"
            ) { dialog, _ ->
                val i = Intent(Intent.ACTION_SEND)
                i.setType("text/plain")
                i.putExtra(Intent.EXTRA_TEXT, textData)
                startActivity(i)
                dialog.dismiss()
            }
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소") {
                dialog, _ -> dialog.dismiss()
            }
            alertDialog.show()
        }
    }
}