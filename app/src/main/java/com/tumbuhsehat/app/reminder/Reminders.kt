package com.tumbuhsehat.app.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.tumbuhsehat.app.MainActivity
import com.tumbuhsehat.app.R
import java.util.concurrent.TimeUnit

object NotificationHelper {
    const val CHANNEL_ID = "pengingat_tumbuhsehat"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Pengingat KawalTumbuh",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Pengingat jadwal imunisasi, checkup, dan gizi"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}

/**
 * One-shot worker that fires a single local notification. Scheduled with an
 * initial delay by [ReminderScheduler] - this is the "in-app reminder"
 * mechanism the project decided to build first (WhatsApp integration is a
 * separate, later addition and is NOT implemented here).
 */
class ReminderWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: "Pengingat KawalTumbuh"
        val body = inputData.getString(KEY_BODY) ?: ""
        val notificationId = inputData.getInt(KEY_NOTIF_ID, System.currentTimeMillis().toInt())

        NotificationHelper.ensureChannel(applicationContext)

        val launchIntent = android.content.Intent(applicationContext, MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            applicationContext,
            notificationId,
            launchIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(applicationContext, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (androidx.core.content.ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS,
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
        }

        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_NOTIF_ID = "notif_id"
    }
}

class ReminderScheduler(private val context: Context) {

    /** Schedules a one-time local notification to fire at [triggerAtMillis]. Returns the WorkManager request id. */
    fun schedule(title: String, body: String, triggerAtMillis: Long, notificationId: Int): String {
        val delay = (triggerAtMillis - System.currentTimeMillis()).coerceAtLeast(0)
        val data: Data = workDataOf(
            ReminderWorker.KEY_TITLE to title,
            ReminderWorker.KEY_BODY to body,
            ReminderWorker.KEY_NOTIF_ID to notificationId,
        )
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()
        WorkManager.getInstance(context).enqueue(request)
        return request.id.toString()
    }

    fun cancel(workRequestId: String) {
        WorkManager.getInstance(context).cancelWorkById(java.util.UUID.fromString(workRequestId))
    }
}
