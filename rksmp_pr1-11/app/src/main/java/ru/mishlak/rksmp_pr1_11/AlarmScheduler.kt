package ru.mishlak.rksmp_pr1_11

// AlarmScheduler.kt
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

	private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

	fun scheduleDailyReminder() {
		val intent = Intent(context, ReminderReceiver::class.java)
		val pendingIntent = PendingIntent.getBroadcast(
			context,
			0,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val calendar = Calendar.getInstance().apply {
			set(Calendar.HOUR_OF_DAY, 20)
			set(Calendar.MINUTE, 0)
			set(Calendar.SECOND, 0)
			set(Calendar.MILLISECOND, 0)
			if (timeInMillis <= System.currentTimeMillis()) {
				add(Calendar.DAY_OF_YEAR, 1)
			}
		}

		// Используем setAlarmClock для максимальной точности и отображения значка
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			alarmManager.setAlarmClock(
				AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent),
				pendingIntent
			)
		} else {
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
		}
	}

	fun cancelReminder() {
		val intent = Intent(context, ReminderReceiver::class.java)
		val pendingIntent = PendingIntent.getBroadcast(
			context,
			0,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		alarmManager.cancel(pendingIntent)
		pendingIntent.cancel()
	}
}