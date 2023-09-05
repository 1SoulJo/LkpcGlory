package com.lkpc.android.app.glory.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.api_client.BasicRequestClient
import com.lkpc.android.app.glory.constants.Notification
import com.lkpc.android.app.glory.ui.detail.DetailActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class LpcMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "LpcMessagingService"
    }

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels()
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationId: Int = Random().nextInt(60000)

        // pending intent
        val resultIntent = Intent(this, DetailActivity::class.java)
        resultIntent.putExtra("singleContentId", remoteMessage.data["contents"])

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // build and show notification
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this, Notification.CHANNEL_ID).apply {
            setContentIntent(resultPendingIntent)
            setContentTitle(remoteMessage.notification!!.title)
            setContentText(remoteMessage.notification!!.body)
            setSmallIcon(R.drawable.lpc_logo_symbol)
            setAutoCancel(true)
            setSound(defaultSoundUri)
        }

        // check if notification contains an image
        if (remoteMessage.notification!!.imageUrl != null) {
            BasicRequestClient().getImage(
                remoteMessage.notification!!.imageUrl.toString(),
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>, res: Response<ResponseBody>) {
                        if (res.isSuccessful && res.body() != null) {
                            val bitmap = BitmapFactory.decodeStream(res.body()!!.byteStream())
                            builder.setLargeIcon(bitmap)
                                .setStyle(NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap))
//                                .bigLargeIcon(null))
                            notificationManager.notify(notificationId, builder.build())
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) { }
                })
        } else {
            notificationManager.notify(notificationId, builder.build())
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannelName: CharSequence = getString(R.string.notification_channel_name)
        val adminChannelDescription = getString(R.string.notification_channel_description)
        val adminChannel = NotificationChannel(
            Notification.CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_LOW
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager.createNotificationChannel(adminChannel)
    }
}