package ru.mishlak.rksmp_pr1_11

// ReminderReceiver.kt
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent?) {
		showNotification(context)
		val scheduler = AlarmScheduler(context)
		scheduler.scheduleDailyReminder()
	}

	private fun showNotification(context: Context) {
		val channelId = "pill_reminder_channel"
		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				channelId,
				"Напоминание о таблетке",
				NotificationManager.IMPORTANCE_HIGH
			).apply {
				description = "Ежедневное напоминание в 20:00"
			}
			notificationManager.createNotificationChannel(channel)
		}

		val notification = NotificationCompat.Builder(context, channelId)
			.setContentTitle("Напоминание")
			.setContentText("Время принять таблетку!")
			.setSmallIcon(android.R.drawable.ic_dialog_info)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setAutoCancel(true)
			.build()

		notificationManager.notify(1, notification)
	}
}