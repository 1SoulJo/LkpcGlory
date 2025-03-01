package com.lkpc.android.app.glory.repository

import androidx.lifecycle.MutableLiveData
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.api_client.ContentApiClient
import com.lkpc.android.app.glory.entity.MeditationV2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

object MeditationRepositoryV2: Callback<List<MeditationV2>> {
    private val apiClient: ContentApiClient = ContentApiClient()

    var data: MutableLiveData<List<MeditationV2>> = MutableLiveData<List<MeditationV2>>()

    fun initData() {
        val date = Calendar.getInstance()
        date.set(Calendar.DATE, 31)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
        if (data.value == null || data.value?.isEmpty() == true) {
            apiClient.loadMeditationsV2(0, format.format(date.time), this@MeditationRepositoryV2)

            val currentMonth = date.get(Calendar.MONTH)
            date.set(Calendar.MONTH, currentMonth + 1)
            date.set(Calendar.DATE, 31)
            apiClient.loadMeditationsV2(0, format.format(date.time), this@MeditationRepositoryV2)

            date.set(Calendar.MONTH, currentMonth - 1)
            date.set(Calendar.DATE, 31)
            apiClient.loadMeditationsV2(0, format.format(date.time), this@MeditationRepositoryV2)
        }
    }

    fun addData(startId: Int) {
        val date = Calendar.getInstance().time
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
        apiClient.loadMeditationsV2(startId, format.format(date), this)
    }

    override fun onResponse(
        call: Call<List<MeditationV2>>, response: Response<List<MeditationV2>>
    ) {
        if (response.isSuccessful) {
            if (data.value == null) {
                data.value = response.body()
            } else {
                val list = (data.value as List<MeditationV2>).toMutableList()
                if (list.isNotEmpty()) list.removeLastOrNull()

                list += response.body() as List<MeditationV2>
                data.value = list
            }
        }
    }

    override fun onFailure(call: Call<List<MeditationV2>>, t: Throwable) {
        t.printStackTrace()
    }
}