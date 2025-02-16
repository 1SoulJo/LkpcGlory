package com.lkpc.android.app.glory.api_client

import com.google.gson.GsonBuilder
import com.lkpc.android.app.glory.constants.WebUrls
import com.lkpc.android.app.glory.entity.AdContent
import com.lkpc.android.app.glory.entity.BaseContent
import com.lkpc.android.app.glory.entity.MeditationV2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ContentApiClient {

    private fun setupApi(): ContentApi {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(WebUrls.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(ContentApi::class.java)
    }

    fun loadColumns(startId: Int, cb: Callback<List<BaseContent>>) {
        setupApi().loadColumns(id=startId).enqueue(cb)
    }

    fun loadSermons(startId: Int, cb: Callback<List<BaseContent>>) {
        setupApi().loadSermons(id=startId).enqueue(cb)
    }

    fun loadMeditations(startId: Int, cb: Callback<List<BaseContent>>) {
        setupApi().loadMeditations(id=startId).enqueue(cb)
    }

    fun loadMeditationsV2List(startId: Int, cb: Callback<List<MeditationV2>>) {
        setupApi().loadMeditationList(id=startId).enqueue(cb)
    }

    fun loadNews(startId: Int, cb: Callback<List<BaseContent>>) {
        setupApi().loadNews(id=startId).enqueue(cb)
    }

    fun loadBulletins(startId: Int, cb: Callback<List<BaseContent>>) {
        setupApi().loadBulletins(id=startId).enqueue(cb)
    }

    fun loadDowntownBulletins(startId: Int, cb: Callback<List<BaseContent>>) {
        setupApi().loadDowntownBulletins(id=startId).enqueue(cb)
    }

    fun loadServices(cb: Callback<BaseContent>) {
        setupApi().loadService().enqueue(cb)
    }

    fun loadLocations(cb: Callback<List<BaseContent>>) {
        setupApi().loadLocation().enqueue(cb)
    }

    fun loadAdContents(cb: Callback<List<AdContent>>) {
        setupApi().loadAdContents().enqueue(cb)
    }

    fun loadSingleContent(contentId: String, cb: Callback<BaseContent>) {
        setupApi().loadSingleContent(id=contentId).enqueue(cb)
    }

    fun increaseViewCount(contentId: String) {
        setupApi().increaseViewCount(contentId).enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) { }
            override fun onFailure(call: Call<String>, t: Throwable) { }
        })
    }

    fun loadCellChurch(startId: Int, cb: Callback<List<BaseContent>>) {
        setupApi().loadCellChurch(id=startId).enqueue(cb)
    }

    fun loadFellowNews(startId: Int, cb: Callback<List<BaseContent>>) {
        setupApi().loadFellowNews(id=startId).enqueue(cb)
    }
}
