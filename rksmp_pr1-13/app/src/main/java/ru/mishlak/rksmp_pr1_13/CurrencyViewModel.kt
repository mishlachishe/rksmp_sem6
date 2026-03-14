package ru.mishlak.rksmp_pr1_13

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class CurrencyViewModel : ViewModel() {
	private val _rate = MutableStateFlow(90.5)
	val rate: StateFlow<Double> = _rate.asStateFlow()
	private var previousRate = _rate.value
	private val _direction = MutableStateFlow(0)
	val direction: StateFlow<Int> = _direction.asStateFlow()
	init {
		startAutoUpdate()
	}
	private fun startAutoUpdate() {
		viewModelScope.launch {
			while (true) {
				delay(5000) // 5 секунд
				generateNewRate()
			}
		}
	}
	fun refreshRate() {
		viewModelScope.launch {
			generateNewRate()
		}
	}
	private fun generateNewRate() {
		val base = 90.5
		val newRate = base + (Random.nextDouble() * 4.0 - 2.0) // диапазон [88.5, 92.5]
		updateRate(newRate)
	}
	private fun updateRate(newRate: Double) {
		val oldRate = _rate.value
		_rate.value = newRate

		_direction.update {
			when {
				newRate > oldRate -> 1
				newRate < oldRate -> -1
				else -> 0
			}
		}
		previousRate = newRate
	}
}