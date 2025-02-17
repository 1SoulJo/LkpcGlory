package com.lkpc.android.app.glory.ui.meditation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.entity.MeditationV2
import com.lkpc.android.app.glory.repository.MeditationRepository
import com.lkpc.android.app.glory.repository.MeditationRepositoryV2

class MeditationViewModelV2 : ViewModel() {

    val currentModel = MutableLiveData<MeditationV2?>()
    val currentTextSize = MutableLiveData<Float>()

    private var currentTextSizeIndex: Int = 0

    private val textSizeList = listOf(18f, 24f, 36f)

    init {
        initData()
    }

    fun getData(): LiveData<List<MeditationV2>> {
        return MeditationRepositoryV2.data
    }

    private fun initData() {
        MeditationRepositoryV2.initData()
    }

    fun addData(id: Int) {
        MeditationRepositoryV2.addData(id)
    }

    fun setCurrentModel(model: MeditationV2) {
        currentModel.value = model
    }

    fun onTextSizeButtonClicked() {
        currentTextSizeIndex = (currentTextSizeIndex + 1) % 3
        currentTextSize.value = textSizeList[currentTextSizeIndex]
    }
}