package com.cs407.noteapp_v2.data
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Date
import java.util.TreeMap

private const val NOTIFICATION_TITLE_FALLBACK = "Reminder"
const val CHANNEL_ID_REMINDER = "channel_reminder"


class ReminderWorker(appContext: Context, params: WorkerParameters)
    : CoroutineWorker(appContext, params) {

    // Same as Zybooks: cache a NotificationManager
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        // Ordered by (remindTime, noteId)
        private val reminderMap = TreeMap<TreeNode, NodeContent>(nodeCompare)
        private val mutex = Mutex()

        // --- safe helpers used by UI / repositories ---

        suspend fun checkEmpty(): Boolean = mutex.withLock { reminderMap.isEmpty() }

        suspend fun readSmallestKey(): TreeNode = mutex.withLock { reminderMap.firstKey() }

        suspend fun readValue(key: TreeNode): NodeContent =
            mutex.withLock { reminderMap.getOrDefault(key, NodeContent()) }

        suspend fun upsertReminder(key: TreeNode, note: NodeContent) {
            mutex.withLock {
                removeReminderWithIDNoLock(key.noteId) // ensure unique by noteId
                reminderMap[key] = note
            }
        }

        suspend fun removeReminder(key: TreeNode) {
            mutex.withLock { reminderMap.remove(key) }
        }

        suspend fun removeReminderWithID(noteID: Int) {
            mutex.withLock { removeReminderWithIDNoLock(noteID) }
        }

        // --- internal: must be called under mutex ---
        private fun removeReminderWithIDNoLock(noteID: Int) {
            var removingKey: TreeNode? = null
            reminderMap.forEach { (k, _) ->
                if (k.noteId == noteID) {
                    removingKey = k
                    return@forEach
                }
            }
            if (removingKey != null) reminderMap.remove(removingKey)
        }
    }


    override suspend fun doWork(): Result {
        createReminderNotificationChannel()

        // Poll forever (assignment spec)
        while (true) {
            // If nothing scheduled, just sleep
            if (!checkEmpty()) {
                val smallest = readSmallestKey()
                val now = Date()
                // If it's due (remindTime <= now), notify & remove it
                if (!smallest.remindTime.after(now)) {
                    val content = readValue(smallest)
                    postReminderNotification(
                        noteId = smallest.noteId,
                        title  = content.title,
                        text   = content.abstract
                    )
                    removeReminder(smallest)
                }
            }
            // Check every 1 second
            delay(1000)
        }
        // (Unreached in this assignment; WorkManager will eventually stop it)
        // return Result.success()
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

data class TreeNode(
    val remindTime: Date,
    val noteId: Int
)

val nodeCompare = Comparator<TreeNode> { a, b ->
    val t = a.remindTime.compareTo(b.remindTime)
    if (t != 0) t else a.noteId.compareTo(b.noteId)
}

data class NodeContent(
    val title: String = "",
    val abstract: String = "",
    val priority: Int = -2
)


// TODO: milestone 3