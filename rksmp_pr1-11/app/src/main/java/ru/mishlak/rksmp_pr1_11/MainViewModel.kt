package ru.mishlak.rksmp_pr1_11

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
	private val context = getApplication<Application>()
	private val prefs = ReminderPreferences(context)
	private val scheduler = AlarmScheduler(context)
	private val _isEnabled = MutableStateFlow(false)
	val isEnabled: StateFlow<Boolean> = _isEnabled
	private val _nextReminderText = MutableStateFlow("")
	val nextReminderText: StateFlow<String> = _nextReminderText
	init {
		viewModelScope.launch {
			prefs.isEnabledFlow.collect { enabled ->
				_isEnabled.value = enabled
				updateNextReminderText(enabled)
			}
		}
	}

	fun toggleReminder(hasPermission: Boolean) {
		viewModelScope.launch {
			val newState = !_isEnabled.value
			if (newState && hasPermission) {
				scheduler.scheduleDailyReminder()
				prefs.setEnabled(true)
			} else if (!newState) {
				scheduler.cancelReminder()
				prefs.setEnabled(false)
			}
		}
	}

	private fun updateNextReminderText(enabled: Boolean) {
		_nextReminderText.value = if (enabled) {
			val calendar = Calendar.getInstance().apply {
				set(Calendar.HOUR_OF_DAY, 20)
				set(Calendar.MINUTE, 0)
				set(Calendar.SECOND, 0)
				set(Calendar.MILLISECOND, 0)
				if (timeInMillis <= System.currentTimeMillis()) {
					add(Calendar.DAY_OF_YEAR, 1)
				}
			}
			val day = if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
				"сегодня" else "завтра"
			val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
			"Следующее напоминание: $day в $time"
		} else {
			""
		}
	}
}