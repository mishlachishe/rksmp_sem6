package ru.mishlak.rksmp_pr1_9

data class CityWeather(
	val name: String,
	var temperature: Int? = null,
	var status: CityStatus = CityStatus.PENDING
)

enum class CityStatus {
	PENDING,
	LOADING,
	LOADED,
	ERROR
}