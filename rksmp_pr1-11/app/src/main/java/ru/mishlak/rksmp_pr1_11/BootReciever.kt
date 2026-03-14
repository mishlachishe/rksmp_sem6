package ru.mishlak.rksmp_pr1_11

// BootReceiver.kt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull

class BootReceiver : BroadcastReceiver() {

	override fun onReceive(context: Context, intent: Intent?) {
		if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
			CoroutineScope(Dispatchers.IO).launch {
				val prefs = ReminderPreferences(context)
				val isEnabled = prefs.isEnabledFlow.firstOrNull() ?: false
				if (isEnabled) {
					AlarmScheduler(context).scheduleDailyReminder()
				}
			}
		}
	}
}