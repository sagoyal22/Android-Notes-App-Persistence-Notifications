package com.cs407.noteapp_v2.data
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

private const val NOTIFICATION_TITLE_FALLBACK = "Reminder"

class ReminderWorker(appContext: Context, params: WorkerParameters)
    : CoroutineWorker(appContext, params) {

    // Same as Zybooks: cache a NotificationManager
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        // 1) make sure channel exists
        createReminderNotificationChannel()

        // 2) when a reminder is due, post one (example call)
        //    You’ll call this with your real data:
        //    postReminderNotification(noteId, title, abstract)
        return Result.success()
    }

    /** Zybooks-style channel creation */
    private fun createReminderNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_REMINDER,                 // "channel_reminder"
                "Reminders",                         // channel name (shown in settings)
                NotificationManager.IMPORTANCE_DEFAULT // DEFAULT: plays sound
            ).apply {
                description = "Notifications for note reminders"
            }
            // Register channel
            notificationManager.createNotificationChannel(channel)
        }
    }

    /** Zybooks-style post function (noteId is the notification id) */
    private fun postReminderNotification(
        noteId: Int,
        title: String?,
        text: String?
    ) {
        val n = NotificationCompat.Builder(applicationContext, CHANNEL_ID_REMINDER)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title?.ifBlank { NOTIFICATION_TITLE_FALLBACK } ?: NOTIFICATION_TITLE_FALLBACK)
            .setContentText(text?.ifBlank { "You have a note to review." } ?: "You have a note to review.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // for Android 7.0 and lower
            .build()

        // Post (only if notifications are enabled)
        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            NotificationManagerCompat.from(applicationContext).notify(noteId, n)
        }
    }
}


// TODO: milestone 3