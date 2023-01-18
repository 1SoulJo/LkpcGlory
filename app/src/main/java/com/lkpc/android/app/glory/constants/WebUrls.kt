package com.lkpc.android.app.glory.constants

class WebUrls {
    companion object {
        // lkpc base url
        const val LKPC_BASE_URL = "https://lkpc.org"

        // board api url
        const val API_BASE_URL = "$LKPC_BASE_URL/node/api/"

        // youtube links
        const val LKPC_LIVE_VIDEO = "https://www.youtube.com/channel/UCe5baq_bVJrXTIhyjXw5HtA/live"
        const val LKPC_CHANNEL = "https://www.youtube.com/channel/UCe5baq_bVJrXTIhyjXw5HtA"
        const val EC_CHANNEL = "https://www.youtube.com/channel/UCMEZLf7qIDm-9C5bJ_pE1HQ"
        const val NHF_CHANNEL = "https://www.youtube.com/channel/UCr1uydfKVRVUqVKkJG9H4dQ"
        const val DT_CHANNEL = "https://www.youtube.com/channel/UCanX-s_RVmdmSLrYBq0F2_w"
        const val YA_CHANNEL = "https://www.youtube.com/channel/UC6dGYIfIarjdeOW4A3FBvPA"

        // newcomer registration
        const val NEWCOMER_REG =
            "https://docs.google.com/forms/d/e/1FAIpQLSd21jJeT_dCyGR-5l-Vbbysh4cgYZYYw_7-owY-CD1g9tRkzQ/viewform"

        // visit registration
        const val VISIT_REG =
            "https://docs.google.com/forms/d/e/1FAIpQLSfMsF1WhN7fvrWscmqyW8Il4h2QR8BlHdobpiuleuw149TX5w/viewform"

        // online meeting registration
        const val ONLINE_MEET_REG = "https://docs.google.com/forms/d/e/1FAIpQLSdbtDxtQAn9D9abIkBe91tYVfoWQSvV37xtwr9-8FH3nECdhA/viewform"

        // online donate
        const val ONLINE_OFFERING = "https://tithe.ly/give_new/www/#/tithely/give-one-time/1277349"

        // LPC home
        const val LPC_HOMEPAGE = "https://lkpc.org"

        // youtube img url
        const val YOUTUBE_IMG_BASE = "https://img.youtube.com/vi/"

        // event api base
        const val EVENT_API_BASE = "http://calendar.lkpc.org/"

        // pdf base
        const val PDF_BASE = "https://docs.google.com/gview?url=http://lkpc.org/assets/board/files/%s&embedded=true"

        // img asset prefix
        const val IMG_ASSET = "$LKPC_BASE_URL/assets/images/%s"

        // sermon audio prefix
        const val SERMON_AUDIO_SRC = "$LKPC_BASE_URL/media/%s"

        // file asset prefix
        const val FILE_ASSET = "$LKPC_BASE_URL/assets/board/files/%s"

        // single bulletin item
        const val RECENT_BULLETIN = "https://docs.google.com/viewerng/viewer?url=https://lkpc.org/book/bulletin&embedded=true"
    }
}