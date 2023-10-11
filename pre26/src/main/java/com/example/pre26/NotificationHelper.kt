package com.example.pre26

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.RemoteInput

class NotificationHelper(private val context: Context) {

    fun showNotification() {
        val builder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOnlyAlertOnce(true)
            .setStyle(
                NotificationCompat
                    .MessagingStyle("You")
                    .addMessage(
                        NotificationCompat.MessagingStyle.Message(
                            "Hello",
                            0L,
                            "Carl Denson"
                        )
                    )
            )
            .addAction(
                NotificationCompat.Action
                    .Builder(
                        R.drawable.ic_launcher_foreground,
                        "Reply",
                        PendingIntent.getBroadcast(
                            context,
                            ReplyReceiver.REQUEST_CONTENT,
                            Intent(context, ReplyReceiver::class.java),
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    )
                    .addRemoteInput(
                        RemoteInput.Builder(ReplyReceiver.KEY_TEXT_REPLY)
                            .setLabel("Enter reply")
                            .build()
                    )
                    .build()
            )

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    companion object {
        var notificationId = 0
    }
}