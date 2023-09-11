package com.lkpc.android.app.glory.ui.bulletin

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.repository.BulletinRepository
import com.lkpc.android.app.glory.repository.DowntownBulletinRepository


class BulletinViewModel(private val isDowntown: Boolean) : ViewModel() {

    init {
        initData()
    }

    fun getData(): LiveData<List<BaseContent>> {
        if (isDowntown) {
            return DowntownBulletinRepository.data
        }
        return BulletinRepository.data
    }

    private fun initData() {
        if (isDowntown) {
            DowntownBulletinRepository.initData()
        } else {
            BulletinRepository.initData()
        }
    }

    fun addData(id: Int) {
        if (isDowntown) {
            DowntownBulletinRepository.addData(id)
        } else {
            BulletinRepository.addData(id)
        }
    }
}

class BulletinViewModelFactory(param: Boolean) : ViewModelProvider.Factory {
    private val mParam: Boolean = param
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BulletinViewModel(mParam) as T
    }
}