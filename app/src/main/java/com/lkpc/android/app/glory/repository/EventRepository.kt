package com.lkpc.android.app.glory.repository

import androidx.lifecycle.MutableLiveData
import com.lkpc.android.app.glory.api_client.EventApiClient
import com.lkpc.android.app.glory.entity.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object EventRepository: Callback<List<Event>> {
    private val apiClient: EventApiClient = EventApiClient()

    var data: MutableLiveData<List<Event>> = MutableLiveData<List<Event>>()

    fun initData() {
        if (data.value == null || data.value!!.isEmpty()) {
            apiClient.getAllEvents(this)
        }
    }

    override fun onResponse(
        call: Call<List<Event>>, response: Response<List<Event>>) {
        if (response.isSuccessful) {
            if (data.value == null) {
                data.value = response.body()
            } else {
                val list = (data.value as List<Event>).toMutableList()
                list += response.body() as List<Event>
                data.value = list
            }
        }
    }

    override fun onFailure(call: Call<List<Event>>, t: Throwable) {
        t.printStackTrace()
    }
}
