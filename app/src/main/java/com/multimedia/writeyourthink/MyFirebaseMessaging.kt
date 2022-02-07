package com.multimedia.writeyourthink

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.widget.RemoteViews
import com.multimedia.writeyourthink.R
import android.content.Intent
import com.multimedia.writeyourthink.MainActivity
import android.app.PendingIntent
import android.media.RingtoneManager
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging

class MyFirebaseMessaging : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.size > 0) {
            showNotification(remoteMessage.data["title"], remoteMessage.data["body"])
        }
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }

    private fun getCustomDesign(title: String?, message: String?): RemoteViews {
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.popup)
        remoteViews.setTextViewText(R.id.noti_title, title)
        remoteViews.setTextViewText(R.id.noti_message, message)
        remoteViews.setImageViewResource(R.id.logo, R.drawable.radius)
        return remoteViews
    }

    fun showNotification(title: String?, message: String?) {
        //팝업 터치시 이동할 액티비티를 지정합니다.
        val intent = Intent(this, MainActivity::class.java)
        //알림 채널 아이디 : 본인 하고싶으신대로...
        val channel_id = "CHN_ID"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        //기본 사운드로 알림음 설정. 커스텀하려면 소리 파일의 uri 입력
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder = NotificationCompat.Builder(applicationContext, channel_id)
            .setSmallIcon(R.drawable.com_facebook_profile_picture_blank_portrait)
            .setSound(uri)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000)) //알림시 진동 설정 : 1초 진동, 1초 쉬고, 1초 진동
            .setOnlyAlertOnce(true) //동일한 알림은 한번만.. : 확인 하면 다시 울림
            .setContentIntent(pendingIntent)
        builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { //안드로이드 버전이 커스텀 알림을 불러올 수 있는 버전이면
                //커스텀 레이아웃 호출
                builder.setContent(getCustomDesign(title, message))
            } else { //아니면 기본 레이아웃 호출
                builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.com_facebook_button_icon) //커스텀 레이아웃에 사용된 로고 파일과 동일하게..
            }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //알림 채널이 필요한 안드로이드 버전을 위한 코드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channel_id, "CHN_NAME", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setSound(uri, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        //알림 표시 !
        notificationManager.notify(0, builder.build())
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