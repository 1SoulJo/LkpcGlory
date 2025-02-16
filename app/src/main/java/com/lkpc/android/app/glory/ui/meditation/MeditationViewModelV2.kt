package com.lkpc.android.app.glory.ui.meditation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.entity.MeditationV2
import com.lkpc.android.app.glory.repository.MeditationRepository
import com.lkpc.android.app.glory.repository.MeditationRepositoryV2

class MeditationViewModelV2 : ViewModel() {
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
}