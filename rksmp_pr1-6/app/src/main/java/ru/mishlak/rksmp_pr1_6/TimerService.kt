package ru.mishlak.rksmp_pr1_6

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

class TimerService : Service() {

	private val handler = Handler(Looper.getMainLooper())
	private val channelId = "timer_channel"
	private val notificationId = 1

	override fun onBind(intent: Intent): IBinder? = null

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		val seconds = intent?.getLongExtra("seconds", 0) ?: 0
		if (seconds > 0) {
			handler.postDelayed({
				showNotification()
				stopSelf()
			}, seconds * 1000)
		} else {
			stopSelf()
		}
		return START_NOT_STICKY
	}

	private fun showNotification() {
		createNotificationChannel()

		val notification = NotificationCompat.Builder(this, channelId)
			.setContentTitle("Таймер завершён!")
			.setContentText("Время вышло")
			.setSmallIcon(R.mipmap.ic_launcher)
			.setAutoCancel(true)
			.build()

		val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		manager.notify(notificationId, notification)
	}

	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				channelId,
				"Timer Notifications",
				NotificationManager.IMPORTANCE_DEFAULT
			)
			val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
			manager.createNotificationChannel(channel)
		}
	}
}