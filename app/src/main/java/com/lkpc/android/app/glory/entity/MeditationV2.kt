package com.lkpc.android.app.glory.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MeditationV2 {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("bible1")
    @Expose
    var bible1: String? = null

    @SerializedName("bible2")
    @Expose
    var bible2: String? = null

    @SerializedName("bible3")
    @Expose
    var bible3: String? = null

    @SerializedName("prayList")
    @Expose
    var prayList: List<String>? = null

    @SerializedName("reflectionList")
    @Expose
    var reflectionList: List<String>? = null

    @SerializedName("applyList")
    @Expose
    var applyList: List<String>? = null

    @SerializedName("videoLink")
    @Expose
    var videoLink: String? = null

    @SerializedName("scheduledDate")
    @Expose
    var location: String? = null
}