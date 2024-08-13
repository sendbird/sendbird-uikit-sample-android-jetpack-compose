package com.sendbird.uikit.compose.sample.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import com.sendbird.android.SendbirdChat.markAsDelivered
import com.sendbird.android.push.SendbirdPushHandler
import com.sendbird.uikit.compose.sample.MainActivity
import com.sendbird.uikit.compose.sample.R
import com.sendbird.uikit.compose.theme.PrimaryMain
import com.sendbird.uikit.core.logger.Logger
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicReference

/**
 * Concrete implementation of a sendbird push handler.
 */
class MyFirebaseMessagingService : SendbirdPushHandler() {
    override val isUniquePushToken: Boolean
        get() = false

    override fun onNewToken(newToken: String?) {
        Logger.i("{$TAG onNewToken($newToken)")
        pushToken.set(newToken)
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(context: Context, remoteMessage: RemoteMessage) {
        Logger.d("From: " + remoteMessage.from)
        if (remoteMessage.data.isNotEmpty()) {
            Logger.d("Message data payload: " + remoteMessage.data)
        }

        // Check if message contains a notification payload.
        Logger.d("Message Notification Body: " + remoteMessage.notification?.body)
        try {
            if (remoteMessage.data.containsKey(sendbird)) {
                val jsonStr = remoteMessage.data[sendbird]
                try {
                    markAsDelivered(remoteMessage.data)
                } catch (e: Exception) {
                    Logger.e(e)
                }
                if (jsonStr == null) return
                sendNotification(context, JSONObject(jsonStr))
            }
        } catch (e: JSONException) {
            Logger.e(e)
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private val pushToken = AtomicReference<String?>()
        const val sendbird = "sendbird"
        const val channel = "channel"
        const val channel_url = "channel_url"
        const val message_id = "message_id"
        const val message = "message"
        const val sender = "sender"
        const val name = "name"
        const val push_title = "push_title"
        const val KEY_CHANNEL_URL = "KEY_CHANNEL_URL"
        const val CHANNEL_NAME = "CHANNEL_NAME"
        const val CHANNEL_ID = "CHANNEL_ID"
        const val PUSH_REDIRECT_CHANNEL = "PUSH_REDIRECT_CHANNEL"
        const val PUSH_REDIRECT_MESSAGE_ID = "PUSH_REDIRECT_MESSAGE_ID"

        /**
         * Create and show a simple notification containing the received FCM message.
         *
         * @param sendBird JSONObject payload from FCM
         */
        @SuppressLint("ResourceType")
        @Throws(JSONException::class)
        fun sendNotification(context: Context, sendBird: JSONObject) {
            val message = sendBird.getString(message)
            val channel = sendBird.getJSONObject(channel)
            val channelUrl = channel.getString(channel_url)
            val messageId = sendBird.getLong(message_id)
            var pushTitle = context.getString(R.string.app_name)
            if (sendBird.has(sender)) {
                val sender = sendBird.getJSONObject(sender)
                pushTitle = sender.getString(name)
            }
            if (sendBird.has(push_title) && !sendBird.isNull(push_title)) {
                pushTitle = sendBird.getString(push_title)
            }

            val channelId = CHANNEL_ID
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).let {
                    notificationManager.createNotificationChannel(it)
                }
            }

            val intent = newRedirectToChannelIntent(context, channelUrl, messageId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            @SuppressLint("UnspecifiedImmutableFlag")
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    messageId.toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.icon_chat_filled)
                .setColor(PrimaryMain.toArgb()) // small icon background color
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.icon_chat_filled))
                .setContentTitle(pushTitle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setContentText(message)
            notificationManager.notify(System.currentTimeMillis().toString(), 0, notificationBuilder.build())
        }

        private fun newRedirectToChannelIntent(
            context: Context,
            channelUrl: String,
            messageId: Long,
        ): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(KEY_CHANNEL_URL, channelUrl)
                putExtra(PUSH_REDIRECT_CHANNEL, channelUrl)
                putExtra(PUSH_REDIRECT_MESSAGE_ID, messageId)
            }
        }
    }
}
