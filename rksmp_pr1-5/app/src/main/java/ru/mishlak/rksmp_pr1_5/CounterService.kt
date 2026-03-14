package ru.mishlak.rksmp_pr1_5

import android.app.*
import android.content.*
import android.os.*
import androidx.core.app.*
import androidx.localbroadcastmanager.content.*
import kotlinx.coroutines.*

class CounterService : Service() {
	private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob());
	private var counter = 0;
	private val channelId = "counter_channel";
	private val notificationId = 100;
	companion object {
		val ACTION_UPDATE = "ru.mishlak.COUNTER_UPDATE";
		val EXTRA_COUNTER = "counter_value";
	}
	override fun onCreate() {
		super.onCreate();
		createNotificationChannel();
		startForeground(notificationId, createNotification(counter));
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		startCounterLoop();
		return START_NOT_STICKY;
	}

	override fun onBind(p0: Intent?): IBinder? {
		return null;
	}
	private fun startCounterLoop() {
		serviceScope.launch() {
			while (isActive) {
				delay(1000);
				counter++;
				updateNotification();
				sendBroadcastUpdate();
			}
		}
	}
	private fun updateNotification() {
		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
		notificationManager.notify(notificationId, createNotification(counter));
	}
	private fun sendBroadcastUpdate() {
		val intent = Intent(ACTION_UPDATE).apply {
			putExtra(EXTRA_COUNTER, counter);
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
	}
	private fun createNotification(counterValue: Int): Notification {
		val intent = packageManager.getLaunchIntentForPackage(packageName);
		val pendintIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE);
		return NotificationCompat.Builder(this, channelId)
			.setContentTitle("Счётчик времени")
			.setContentText("Прошло $counterValue сек")
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentIntent(pendintIntent)
			.setPriority(NotificationCompat.PRIORITY_LOW)
			.build();
	}
	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				channelId, "Счётчик времени", NotificationManager.IMPORTANCE_LOW
			).apply {
					description = "Показывает текущее значение счётчика";
				}
			val manager = getSystemService(NotificationManager::class.java);
			manager.createNotificationChannel(channel);
		}
	}
	override fun onDestroy() {
		super.onDestroy();
		serviceScope.cancel();
	}
}