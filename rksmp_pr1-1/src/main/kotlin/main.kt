import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.io.*
import kotlin.random.*
import kotlin.system.*

@Serializable
data class User(val id: Int, val name: String)

@Serializable
data class SalesResponse(val today: String, val items: List<SalesItem>)

@Serializable
data class SalesItem(val product: String, val qty: Int, val revenue: Int)

@Serializable
data class Weather(val city: String, val temp: Int, val condition: String)

sealed class TaskRes<out T> {
	data class Success<T>(val data: T) : TaskRes<T>()
	data class Error(val message: String) : TaskRes<Nothing>()
}

suspend fun main() = runBlocking {
	val time = measureTimeMillis {
		println("Запуск параллельных задач")
		val usersDef = async { loadUsers() }
		val salesDef = async { loadSales() }
		val weatherDef = async { loadWeather() }

		val usersRes = usersDef.await()
		val salesRes = salesDef.await()
		val weatherRes = weatherDef.await()
		println("РЕЗУЛЬТАТЫ")
		when (usersRes) {
			is TaskRes.Success -> {
				val names = usersRes.data.map { it.name }
				println("Пользователи -> $names")
			}

			is TaskRes.Error -> println("Ошибка загрузки пользователей -> ${usersRes.message}")
		}
		when (salesRes) {
			is TaskRes.Success -> {
				val map = salesRes.data.items.associate { it.product to it.qty }
				println("Статистика продаж -> $map")
			}

			is TaskRes.Error -> println("Ошибка загрузки продаж -> ${salesRes.message}")
		}
		when (weatherRes) {
			is TaskRes.Success -> {
				val weatherStrings = weatherRes.data.map { "${it.city}: ${it.temp}°C" }
				println("Погода -> $weatherStrings")
			}

			is TaskRes.Error -> println("Ошибка загрузки погоды -> ${weatherRes.message}")
		}
	}
	println("Общее время выполнения -> ${time / 1000.0} сек")
}

suspend fun loadUsers(): TaskRes<List<User>> {
	delay(1800)
	return try {
		if (Random.nextInt(0, 10) < 3) { //рандом 30%
			throw Exception("Сбой")
		}
		val json = File("users.json").readText()
		val users = Json.decodeFromString<List<User>>(json)
		TaskRes.Success(users)
	} catch (e: Exception) {
		TaskRes.Error(e.message ?: "Неизвестная ошибка")
	}
}

suspend fun loadSales(): TaskRes<SalesResponse> {
	delay(1200)
	return try {
		if (Random.nextInt(0, 10) < 3) {
			throw Exception("Сбой")
		}
		val json = File("sales.json").readText()
		val sales = Json.decodeFromString<SalesResponse>(json)
		TaskRes.Success(sales)
	} catch (e: Exception) {
		TaskRes.Error(e.message ?: "Неизвестная ошибка")
	}
}

suspend fun loadWeather(): TaskRes<List<Weather>> {
	delay(2500)
	return try {
		if (Random.nextInt(0, 10) < 3) {
			throw Exception("Сбой")
		}
		val json = File("weather.json").readText()
		val weather = Json.decodeFromString<List<Weather>>(json)
		TaskRes.Success(weather)
	} catch (e: Exception) {
		TaskRes.Error(e.message ?: "Неизвестная ошибка")
	}
}