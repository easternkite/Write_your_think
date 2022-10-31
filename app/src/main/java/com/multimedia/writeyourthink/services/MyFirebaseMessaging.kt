package com.multimedia.writeyourthink.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.widget.RemoteViews
import android.content.Intent
import com.multimedia.writeyourthink.ui.DiaryActivity
import android.app.PendingIntent
import android.media.RingtoneManager
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.multimedia.writeyourthink.R

class MyFirebaseMessaging : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.size > 0) {
            showNotification(remoteMessage.data["title"], remoteMessage.data["body"])
            Log.d("Lee", "title: ${remoteMessage.data["title"]}, ${remoteMessage.data["body"]}")
        }
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("Lee", "onNewToken: $s")
    }

    fun showNotification(title: String?, message: String?) {
        //팝업 터치시 이동할 액티비티를 지정합니다.
        val intent = Intent(this, DiaryActivity::class.java)
        //알림 채널 아이디 : 본인 하고싶으신대로...
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        //기본 사운드로 알림음 설정. 커스텀하려면 소리 파일의 uri 입력
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder = NotificationCompat.Builder(applicationContext, packageName)
            .setSmallIcon(R.drawable.ic_stat_event_note)
            .setColor(ContextCompat.getColor(this, R.color.teal_200))
            .setSound(uri)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000)) //알림시 진동 설정 : 1초 진동, 1초 쉬고, 1초 진동
            .setOnlyAlertOnce(true) //동일한 알림은 한번만.. : 확인 하면 다시 울림
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(0, builder.build())
        }

    }

    init {
        val token = FirebaseMessaging.getInstance().token
        token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM Token", task.result!!)
            }
        }
    }
}