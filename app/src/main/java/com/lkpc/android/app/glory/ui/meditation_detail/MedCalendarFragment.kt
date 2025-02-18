package com.lkpc.android.app.glory.ui.meditation_detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.FragmentMedCalendarBinding
import com.lkpc.android.app.glory.ui.meditation.MeditationViewModelV2
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class MedCalendarFragment : DialogFragment(R.layout.fragment_med_calendar) {

    private var _binding: FragmentMedCalendarBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val noteDecorator: DayViewDecorator = object: DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day == CalendarDay.from(2025, 2, 10)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(DotSpan(10f, Color.BLUE))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.calendarView.selectedDate = CalendarDay.today()
        binding.calendarView.addDecorator(noteDecorator)
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            val viewModel: MeditationViewModelV2 by activityViewModels()
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
            val date2 = format.parse("${date.year}-${date.month}-${date.day}")
            date2?.let { date3 ->
                viewModel.dataMap[format.format(date3)]?.let {
                    viewModel.setCurrentModel(it)
                    dismiss()
                } ?: Toast.makeText(context, "묵상이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params
    }
}