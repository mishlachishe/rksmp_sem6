package ru.mishlak.rksmp_pr1_5

import android.content.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.localbroadcastmanager.content.*


class MainActivity : ComponentActivity() {

	private val broadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			if (intent?.action == CounterService.ACTION_UPDATE) {
				val value = intent.getIntExtra(CounterService.EXTRA_COUNTER, 0)
				_counterState.value = value
			}
		}
	}

	private val _counterState = mutableStateOf(0)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// Регистрируем локальный приёмник
		LocalBroadcastManager.getInstance(this).registerReceiver(
			broadcastReceiver, IntentFilter(CounterService.ACTION_UPDATE)
		)

		setContent {
			CounterScreen(
				counter = _counterState.value,
				onStart = { startCounterService() },
				onStop = { stopCounterService() }
			)
		}
	}

	private fun startCounterService() {
		val intent = Intent(this, CounterService::class.java)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			startForegroundService(intent)
		} else {
			startService(intent)
		}
	}

	private fun stopCounterService() {
		val intent = Intent(this, CounterService::class.java)
		stopService(intent)
	}

	override fun onDestroy() {
		super.onDestroy()
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
	}
}

@Composable
fun CounterScreen(counter: Int, onStart: () -> Unit, onStop: () -> Unit) {
	Column(
		modifier = Modifier.fillMaxSize(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(
			text = counter.toString(),
			fontSize = 72.sp,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.onBackground
		)
		Spacer(modifier = Modifier.height(32.dp))
		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp)
		) {
			Button(onClick = onStart) {
				Text("Старт")
			}
			Button(onClick = onStop) {
				Text("Стоп")
			}
		}
	}
}