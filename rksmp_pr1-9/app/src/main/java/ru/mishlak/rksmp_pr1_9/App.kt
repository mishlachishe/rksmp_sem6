package ru.mishlak.rksmp_pr1_9

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App : Application() {
	override fun onCreate() {
		super.onCreate()
		createNotificationChannel()
	}

	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				"weather_channel",
				"Weather Updates",
				NotificationManager.IMPORTANCE_LOW
			).apply {
				description = "Shows weather collection progress"
			}
			val manager = getSystemService(NotificationManager::class.java)
			manager.createNotificationChannel(channel)
		}
	}
}