package ru.mishlak.rksmp_pr1_9

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random
object WeatherData {
	val cities = MutableStateFlow(
		listOf(
			CityWeather("Москва"),
			CityWeather("Лондон"),
			CityWeather("Нью-Йорк"),
			CityWeather("Токио")
		)
	)
}

class WeatherWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
	private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

	override suspend fun doWork(): Result {
		ensureNotificationChannel()
		setForeground(createForegroundInfo("Начинаем сбор данных..."))
		val total = WeatherData.cities.value.size
		val results = mutableListOf<Pair<String, Int>>()
		WeatherData.cities.value.forEachIndexed { index, city ->
			WeatherData.cities.update { currentList ->
				currentList.toMutableList().apply {
					this[index] = this[index].copy(status = CityStatus.LOADING)
				}
			}
			notificationManager.notify(1, createNotification(
				"Загружаем ${city.name}... ($index/$total)",
				ongoing = true
			))
			delay(Random.nextLong(2000, 5000))
			val temperature = Random.nextInt(10, 30)
			results.add(city.name to temperature)
			WeatherData.cities.update { currentList ->
				currentList.toMutableList().apply {
					this[index] = this[index].copy(
						temperature = temperature,
						status = CityStatus.LOADED
					)
				}
			}
			notificationManager.notify(1, createNotification(
				"Готово: ${city.name} ${temperature}°C (${index + 1}/$total)",
				ongoing = true
			))
		}
		val average = results.map { it.second }.average()
		val finalText = "Готово! Средняя температура: %.1f°C".format(average)

		WeatherData.cities.update { currentList ->
			currentList.toMutableList().apply {
				forEachIndexed { index, city ->
					this[index] = city.copy(status = CityStatus.LOADED)
				}
			}
		}

		notificationManager.notify(1, createNotification(finalText, ongoing = false))

		return Result.success(workDataOf("average" to average))
	}

	private fun ensureNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				"weather_channel",
				"Weather Updates",
				NotificationManager.IMPORTANCE_LOW
			).apply {
				description = "Shows weather collection progress"
			}
			notificationManager.createNotificationChannel(channel)
		}
	}

	private fun createForegroundInfo(text: String): ForegroundInfo {
		val notification = createNotification(text, ongoing = true)
		return ForegroundInfo(1, notification)
	}

	private fun createNotification(text: String, ongoing: Boolean): android.app.Notification {
		return NotificationCompat.Builder(applicationContext, "weather_channel")
			.setContentTitle("Сбор прогноза погоды")
			.setContentText(text)
			.setSmallIcon(android.R.drawable.ic_dialog_info)
			.setOngoing(ongoing)
			.build()
	}
}