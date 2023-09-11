package com.lkpc.android.app.glory.api_client

import com.lkpc.android.app.glory.entity.AdContent
import com.lkpc.android.app.glory.entity.BaseContent
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ContentApi {
    @GET("board/column/{startId}")
    fun loadColumns(@Path("startId") id: Int): Call<List<BaseContent>>

    @GET("board/sermon/{startId}")
    fun loadSermons(@Path("startId") id: Int): Call<List<BaseContent>>

    @GET("board/meditation/{startId}")
    fun loadMeditations(@Path("startId") id: Int): Call<List<BaseContent>>

    @GET("board/news/{startId}")
    fun loadNews(@Path("startId") id: Int): Call<List<BaseContent>>

    @GET("board/bulletin/{startId}")
    fun loadBulletins(@Path("startId") id: Int): Call<List<BaseContent>>

    @GET("board/bulletin-downtown/{startId}")
    fun loadDowntownBulletins(@Path("startId") id: Int): Call<List<BaseContent>>

    @GET("page/service")
    fun loadService(): Call<BaseContent>

    @GET("page/location")
    fun loadLocation(): Call<List<BaseContent>>

    @GET("link/main")
    fun loadAdContents(): Call<List<AdContent>>

    @GET("board/{contentId}")
    fun loadSingleContent(@Path("contentId") id: String): Call<BaseContent>

    @POST("board/{contentId}")
    fun increaseViewCount(@Path("contentId") id: String): Call<String>

    @GET("board/cell-church/{startId}")
    fun loadCellChurch(@Path("startId") id: Int): Call<List<BaseContent>>

    @GET("board/fellow-news/{startId}")
    fun loadFellowNews(@Path("startId") id: Int): Call<List<BaseContent>>
}
