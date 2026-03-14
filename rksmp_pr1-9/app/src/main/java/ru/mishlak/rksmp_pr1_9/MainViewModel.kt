package ru.mishlak.rksmp_pr1_9

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
	private val workManager = WorkManager.getInstance(application)
	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading
	private val _averageTemp = MutableStateFlow<Double?>(null)
	val averageTemp: StateFlow<Double?> = _averageTemp
	val cities: StateFlow<List<CityWeather>> = WeatherData.cities
	fun startWork() {
		viewModelScope.launch {
			_isLoading.value = true
			_averageTemp.value = null
			WeatherData.cities.value = WeatherData.cities.value.map {
				it.copy(temperature = null, status = CityStatus.PENDING)
			}
			val request = OneTimeWorkRequestBuilder<WeatherWorker>().build()
			workManager.enqueue(request)
			workManager.getWorkInfoByIdLiveData(request.id).observeForever { info ->
				if (info != null && info.state.isFinished) {
					_isLoading.value = false
					if (info.state == WorkInfo.State.SUCCEEDED) {
						_averageTemp.value = info.outputData.getDouble("average", 0.0)
					}
				}
			}
		}
	}
}