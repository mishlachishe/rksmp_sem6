package ru.mishlak.rksmp_pr1_7

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

	private val _currentNumber = MutableStateFlow(0)
	val currentNumber: StateFlow<Int> = _currentNumber.asStateFlow()

	private val _isConnected = MutableStateFlow(false)
	val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

	private var boundService: NumberGeneratorService? = null

	private val connection = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			boundService = (service as NumberGeneratorService.LocalBinder).getService()
			boundService?.registerListener { number ->
				_currentNumber.value = number
			}
			_isConnected.value = true
		}

		override fun onServiceDisconnected(name: ComponentName?) {
			boundService = null
			_isConnected.value = false
		}
	}

	fun bindService(context: Context) {
		if (!_isConnected.value) {
			Intent(context, NumberGeneratorService::class.java).also { intent ->
				context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
			}
		}
	}

	fun unbindService(context: Context) {
		if (_isConnected.value) {
			boundService?.unregisterListener()
			context.unbindService(connection)
			_isConnected.value = false
			boundService = null
		}
	}
}